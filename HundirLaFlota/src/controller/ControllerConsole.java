package controller;

import controller.commands.*;
import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import exceptions.GameException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.PlayerDTO;
import view.GameObserver;

import java.util.List;

public class ControllerConsole implements Controller {
    Game game;
    GameObserver printer = null;

    public ControllerConsole(Game game) {
        this.game = game;
    }

    @Override
    public void addObserver(GameObserver observer) {
        game.addObserver(observer);
        if (this.printer == null) this.printer = observer;
    }

    @Override
    public void run() {
        if (!cargar()) {
            getLevelAndPlayers();
            placingPhase();
        }
        attackingPhase();
    }

    private boolean cargar() {
        if (printer.cargar()) {
            String[] s = new String[2];
            s[0] = "load";
            s[1] = printer.getInput("Introduce el nombre del archivo: ")[0];
            try {
                LoadCommand l = new LoadCommand();
                l = (LoadCommand) l.parse(s);
                l.execute(game);
            } catch (GameException e) {
                printer.handleException(e);
            }
            return true;
        } else return false;
    }

    private void getLevelAndPlayers() {
        try {
            parseLevelAndNames(printer.getInput("Choose level and number of players (e.g. EASY 2): "));
        } catch (GameException ge) {
            printer.handleException(ge);
            getLevelAndPlayers();
        }
    }

    private void parseLevelAndNames(String[] args) throws GameException {
        if (args.length == 2) {
            try {
                Level level = Level.parse(args[0]);
                int numPlayers = Integer.parseInt(args[1]);
                String[] bots = printer.getInput("Choose number of bots: ");
                int numBots = Integer.parseInt(bots[0]);
                if (numBots > numPlayers) {
                    throw new CommandParseException("[ERROR]: Incorrect number of bots");
                } 
                
                String[] names = printer.getInput("Write the rest of players' names if applicable: ");
                if(names.length == 1 && numBots == numPlayers) {
                	game.init(level, numPlayers - numBots, numBots, names);
                }
                else if (names.length != numPlayers - numBots)
                    throw new CommandParseException("[ERROR]: Incorrect number of names");
                else{
                	game.init(level, numPlayers - numBots, numBots, names);
                }
            } catch (NumberFormatException nfe) {
                throw new CommandParseException("[ERROR]: Incorrect format for number of players");
            }
        } else throw new CommandParseException("[ERROR]: Incorrect number of arguments for level and players");
    }

    private void attackingPhase() {
        CommandGenerator.setAttackingCommands();
        while (!game.isFinished()) {
            for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
                if (!game.hasLost(i)) {
                    game.setCurrentPlayer(i);
                    System.out.println("-----------TURNO DE " + (i+1) + "--------------");
                    game.updateObservers();
                    if (!game.attack(i)) {
                        manualAttack(i);
                    }
                }
            }
        }
    }

    private void manualAttack(int i) {
        boolean success = false;
        while (!success) {
            String[] parameters = printer.getInput("[" + game.getPlayerID(i) + "]" + " Introduce a command: ");
            try {
                Command command = CommandGenerator.parse(parameters);
                success = command.execute(game);
            } catch (GameException ge) {
                printer.handleException(ge);
            }
        }
    }

    private void placingPhase() {
        CommandGenerator.setPlacingCommands();
        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
            game.setCurrentPlayer(i);

            if (!game.place(i)) {
                manualPlace(i);
            }
            
        }
    }

    private void manualPlace(int i) {
        int colocados = 0;
        while (!game.allShipsPlaced(i)) {
            String[] parameters = printer.getInput("[" + game.getPlayerID(i) + "]" + " Ship with length "
                    + game.getLevel().getShipLength(colocados) + " will be placed. Introduce a command: ");
            try {
                Command command = CommandGenerator.parse(parameters);
                if (command.execute(game))
                    ++colocados;
                game.updateObservers();

            } catch (GameException ge) {
                printer.handleException(ge);
            }
        }
    }

    @Override
    public int getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

	@Override
	public List<PlayerDTO> getListOfPlayers() {
		return null;
	}

	@Override
	public void setConfig(Level level, int realPlayers, int botPlayers, List<String> playerNames) throws CommandExecuteException {
	}

	@Override
	public void placeShip(int currentShip, Position pos, Orientation o) throws CommandExecuteException {
	}

	@Override
	public void attackPos(int currentPlayer, Position position, int player) throws CommandExecuteException {
	}

	@Override
	public Level getLevel() {
		return null;
	}

	@Override
	public void setCurrentShip(int i) {
	}

	@Override
	public void attackBot() {
	}

	@Override
	public void getNotify() {
	}

	@Override
	public boolean getStart() {
		return false;
	}
}
