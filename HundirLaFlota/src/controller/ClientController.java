package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.GameParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.Player;
import model.PlayerDTO;
import view.GameObserver;
import web.Cliente;

public class ClientController implements Controller {
    private final List<GameObserver> observers;
    private final Cliente cliente;
    private final List<PlayerDTO> players;
    Game game;
    private Level level;
    private Player currentPlayer;
    private int currentShip, currentPlayerNum, realPlayers, botPlayers, index;
    private String[] arrayNames;

    public ClientController(String host) throws IOException {
        this.cliente = new Cliente(host);
        this.game = Game.getInstance();
        this.observers = new ArrayList<GameObserver>();
        this.players = new ArrayList<PlayerDTO>();
    }

    @Override
    public void addObserver(GameObserver o) {
        this.observers.add(o);
    }

    @Override
    public List<PlayerDTO> getListOfPlayers() {
        return players;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    /**
     * This method parses and receives the information related to the configuration of the game
     */
    public void parseConfi(JSONObject set) {
        JSONObject config = set.getJSONObject("configuration");
        this.level = Level.Safeparse(config.getString("Level"));
        this.realPlayers = config.has("realplayers") ? config.getInt("realplayers") : null;
        this.botPlayers = config.has("botplayers") ? config.getInt("botplayers") : null;
        JSONArray names = set.getJSONArray("names");
        this.arrayNames = new String[names.length()];
        for (int i = 0; i < names.length(); i++) {
            arrayNames[i] = names.getString(i);
            Player p = new Player(arrayNames[i], level);
            players.add(p.getDTO());
        }
    }

    /**
     * Given a ship, a position and the orientation, this method sends the corresponding information to the server so that it
     * can take care of placing the ship and waits for a notification.
     *
     * @param currentShip - an integer representing the ship
     * @param pos         - the {@code Position} for the ship to be placed
     * @param o           - the {@code Orientation} for the ship to be placed in
     */
    public void placeShip(int currentShip, Position pos, Orientation o) throws CommandExecuteException {
        if (currentShip == -1) throw new CommandExecuteException("No ship selected.");
        DataOutputStream out;
        String s = "{";
        s += "currentShip:" + currentShip + ",";
        s += "position:" + pos.getState().toString() + ",";
        s += "orientation:" + o.getShortcut() + "}";
        try {
            out = new DataOutputStream(cliente.getOutputStream());
            out.writeUTF(s);
            getNotify();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getStart() {
        DataInputStream in;
        try {
            in = new DataInputStream(cliente.getInputStream());
            String datos = in.readUTF();
            JSONObject jsonInput = new JSONObject(new JSONTokener(datos));
            if (jsonInput.has("level")) {
                this.level = Level.Safeparse(jsonInput.getString("level"));
            }
            if (jsonInput.has("index")) {
                this.index = jsonInput.getInt("index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This method is in charge of receiving and parsing notifications sent by the server and taking care of what should be done in the corresponding
     * cases.
     */
    public void getNotify() {
        DataInputStream in;
        String s;
        try {
            in = new DataInputStream(cliente.getInputStream());
            s = in.readUTF();
            if (!s.equals("")) {
                JSONObject jsonInput = new JSONObject(new JSONTokener(s));
                if (jsonInput.has("level")) {
                    this.level = Level.Safeparse(jsonInput.getString("level"));
                } else if (jsonInput.has("phaseChange")) {
                    notifyPhaseChange();
                } else if (jsonInput.has("placedShip")) {
                    parsePlacedShip(jsonInput.getJSONObject("placedShip"));
                } else if (jsonInput.has("savedGame")) {
                    String file = jsonInput.getString("savedGame");
                    savedGame(file);
                } else if (jsonInput.has("endScreen")) {
                    Player player = Player.parse(jsonInput.getJSONObject("endScreen"), level, game);
                    endScreen(player);
                } else if (jsonInput.has("changePlayer")) {
                    int num = jsonInput.getInt("changePlayer");
                    changePlayer(num);

                } else if (jsonInput.has("defeatedPlayer")) {
                    int num = jsonInput.getInt("defeatedPlayer");
                    defeatedPlayer(num);
                } else if (jsonInput.has("set")) {
                    parseConfi(jsonInput.getJSONObject("set"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method receives a JSONObject containing the DTO and the int from a player and notifies the gameObservers
     * that a ship has been placed. Also asks for a notification if the player has placed his last ship
     */
    private void parsePlacedShip(JSONObject jsonObject) throws GameParseException {
        currentPlayerNum = jsonObject.has("currentPlayer") ? jsonObject.getInt("currentPlayer") : null;
        currentPlayer = Player.parse(jsonObject.getJSONObject("playerDTO"), level, game);
        onPlacedShip();
        if (currentPlayer.allShipsPlaced()) {
            getNotify();
        }
    }

    @Override
    public void sendName(String name) {
        try {
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            out.writeUTF(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyPhaseChange() {
        for (GameObserver o : observers)
            o.onPhaseChange();
    }

    private void onPlacedShip() {
        for (GameObserver go : observers)
            go.onPlacedShip(currentPlayerNum, currentPlayer.getDTO());
    }

    private void savedGame(String file) {
        for (GameObserver go : observers)
            go.onSavedGame(file);
    }

    private void endScreen(Player player) {
        for (GameObserver o : observers)
            o.endScreen(player.getDTO());
    }

    private void changePlayer(int player) {
        for (GameObserver o : observers) {
            o.onChangedPlayer(player, currentPlayer.getReal());
        }
    }

    private void defeatedPlayer(int i) {
        for (GameObserver o : observers)
            o.onDefeatedPlayer(i);
    }

    @Override
    public Socket getSocket() {
        return this.cliente.getCs();
    }

    @Override
    public int getIndex() {
        return this.index;
    }


    @Override
    public void setConfig(Level level, int realPlayers, int botPlayers, List<String> playerNames) throws CommandExecuteException {
    }

    @Override
    public int getCurrentPlayer() {
        return 0;
    }

    @Override
    public void attackPos(int currentPlayer, Position position, int player) throws CommandExecuteException {
    }

    @Override
    public void setCurrentShip(int i) {
        try {
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            out.writeUTF("{set:{currentShip: " + i + "}}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentShip = i;
    }
}
