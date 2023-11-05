package controller;

import java.io.*;
import java.util.List;

import exceptions.CommandExecuteException;
import exceptions.GameParseException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.PlayerDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import view.GameObserver;

public class ControllerGUI implements Controller {
    Game game;
    String phase;

    public ControllerGUI(Game game) {
        this.game = game;
        this.phase = "placing";
    }

    public void addObserver(GameObserver o) {
        game.addObserver(o);
    }

    public List<PlayerDTO> getListOfPlayers() {
        return game.getPlayers();
    }

    public Level getLevel() {
        return game.getLevel();
    }

    public void run() {
        int currentPlayer = game.getCurrentPlayer();
        List<PlayerDTO> players = game.getPlayers();
        if (this.phase.equals("placing")) {
            if (players.get(currentPlayer).getAllShipsPlaced()) {// si el jugador actual ya tiene todos los barcos
                // colocados
                game.setCurrentPlayer(currentPlayer + 1);// paso el turno
                if (!players.get(game.getCurrentPlayer()).getReal()) {
                    game.placeCurrentPlayerShips();
                    run();
                }
                if (currentPlayer == players.size() - 1) {// si ya estoy en el ultimo jugador, es decir, todos los
                    // demas// tb tienen sus barcos colocados
                    this.phase = "attacking";
                    game.notifyPhaseChange();
                }
            }
        } else if (this.phase.equals("attacking") && !game.isFinished()) {
            if (!players.get(currentPlayer).hasLost()) {
                if (!players.get(game.getCurrentPlayer()).getReal()
                        && !players.get(game.getCurrentPlayer()).hasLost()) {
                    game.attackBot();
                    game.setCurrentPlayer(game.getCurrentPlayer() + 1);
                    run();
                }
            } else {
                game.setCurrentPlayer(currentPlayer + 1);
                run();
            }
        }
    }

    @Override
    public void setConfig(Level level, int realPlayers, int botPlayers, List<String> playerNames) throws CommandExecuteException {
        String[] array = new String[playerNames.size()];
        for (int i = 0; i < playerNames.size(); ++i) {
            array[i] = playerNames.get(i);
        }
        game.init(level, realPlayers, botPlayers, array);
    }

    @Override
    public void placeShip(int currentShip, Position pos, Orientation o) throws CommandExecuteException {
        if (currentShip != -1)
            game.placeShip(currentShip, pos, o);
        else
            throw new CommandExecuteException("No ship selected.");
        run();
    }

    @Override
    public void attackPos(int currentPlayer, Position position, int player) throws CommandExecuteException {
        if (game.attackShip(player, position)) {
            game.setCurrentPlayer(currentPlayer + 1);
            run();
        }
    }

    @Override
    public int getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    @Override
    public void setCurrentShip(int i) {
        game.setCurrentShip(i);
    }

    @Override
    public void save(String file) {
        game.save(file);
    }

    @Override
    public void load(String file) throws GameParseException, FileNotFoundException {
        InputStream is = new FileInputStream(new File(file));
        JSONObject jsonInput;
        try {
            jsonInput = new JSONObject(new JSONTokener(is));
        } catch (JSONException je) {
            throw new GameParseException();
        }
        game.load(jsonInput);
    }

    @Override
    public void changePhase() {
        this.phase = "attacking";
        game.notifyPhaseChange();
    }

    @Override
    public void reset() {
        this.phase = "placing";
        game.reset();
    }

    @Override
    public void getNotify() {
    }

    @Override
    public boolean getStart() {
        return false;
    }
}
