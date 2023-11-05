package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import exceptions.GameParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Orientation;
import misc.Position;

import view.GameObserver;

public class Game {
    private static Game uniqueGame = null;
    private final List<GameObserver> observers;
    private List<Player> players;
    private Level level;
    private Player currentPlayer;
    private int currentPlayerNum;

    private Game() {
        this.observers = new ArrayList<GameObserver>();
        players = new ArrayList<Player>();
    }

    /**
     * This method returns the already existing instance of {@code Game} if it exists. If not, a new instance is created
     * and returned following the Memento pattern.
     * @return the unique {@code Game} instance.
     */
    public static Game getInstance() {
        if (uniqueGame == null) uniqueGame = new Game();
        return uniqueGame;
    }

    /**
     * Given a {@code Player} and a {@code Position}, this method is in charge to tell the bot whether it attacked
     * succesfully or not and if he was, makes the player receive the shot with everything that conveys.
     *
     * @param player the {@code Player} that is being attacked by a bot.
     * @param pos    the {@code Position} that is being attacked.
     * @return {@code true} if he was able to attack, {@code false} otherwise.
     * @see BotPlayer#attack()
     */
    boolean botAttack(int player, Position pos) {
        boolean result;
        boolean sunk = false;
        if (players.get(player).checkMissOrHit(pos)) {
            result = false;
        } else {
            if (checkPosition(pos)) {
                sunk = players.get(player).receiveShot(pos).get(1);
                result = true;
            } else result = false;
        }
        if (observers != null) {
            for (GameObserver o : observers) {
                o.onAttack(players.get(player).getDTO(), player, pos);
            }
        }

        if (sunk) {
            if (observers != null) {
                for (GameObserver o : observers)
                    o.onSunkShip(currentPlayer.getID(), players.get(player).getID());
            }
        }
        boolean found = false;

        if (players.get(player).hasLost()) {
            int i = 0;
            while (i < players.size() && !found) {
                if (players.get(player).equals(players.get(i)))
                    found = true;
                else ++i;
            }
            if (observers != null) {
                for (GameObserver o : observers)
                    o.onDefeatedPlayer(i);
            }
        }
        return result;
    }

    /**
     * Gets the instance of the {@code Player} represented by an {@code int} indicating its position in the list of players.
     *
     * @param p an {@code int} that indicates the index of the player in the list of players.
     * @return said {@code Player}.
     */
    Player getPlayer(int p) {
        return players.get(p);
    }

    /**
     * Attacks a player's {@code Position} and return wether a {@code Ship} was hit.
     *
     * @param player the {@code Player} to attack.
     * @param pos    the {@code Position} to attack.
     * @return {@code true} if a {@code Ship} was hit.
     * @throws CommandExecuteException if either the position or the attacked player are not valid.
     */
    public boolean attackShip(int player, Position pos) throws CommandExecuteException {
        if (player >= 0 && player < players.size()) {
            attackShip(players.get(player), pos);
            return true;
        } else
            throw new CommandExecuteException("[ERROR]: Incorrect player number");
    }

    /**
     * Makes the {@code currentPlayer}, which is a bot, attack.
     */
    public void attackBot() {
        currentPlayer.attack();
    }

    /**
     * Gets the game's {@code Level}.
     * @return the game's {@code Level}.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Given a {@code Position} and a {@code Player} represented by its name, this method attacks said player.
     *
     * @param p   the {@code String} that represents the player's name.
     * @param pos the {@code Position} to attack.
     * @throws CommandExecuteException if the attack cannot be performed.
     */
    public void attackShip(String p, Position pos) throws CommandExecuteException {
        for (Player c : players) {
            if (c.getID().equals(p)) {
                attackShip(c, pos);
                return;
            }
        }
        throw new CommandExecuteException("[ERROR]: Incorrect player name");
    }

    /**
     * Given a {@code Position} and a {@code Player}, this method attacks said player.
     *
     * @param player the {@code Player} to attack.
     * @param pos    the {@code Position} to attack.
     * @throws CommandExecuteException if the attack cannot be performed.
     */
    public void attackShip(Player player, Position pos) throws CommandExecuteException {
        if (player.equals(currentPlayer)) throw new CommandExecuteException("[ERROR]: You cannot attack yourself");
        int i = 0;
        boolean found = false;
        while (i < players.size() && !found) {
            if (player.equals(players.get(i)))
                found = true;
            else ++i;
        }
        if (!player.hasLost()) {
            boolean sunk = player.attackShip(pos, level);
            for (GameObserver o : observers)
                o.onAttack(player.getDTO(), i, pos);
            if (sunk) {
                for (GameObserver o : observers)
                    o.onSunkShip(currentPlayer.getID(), player.getDTO().getID());//currentPlayerName, attackPlayerName
            }
            if (player.hasLost()) {
                if (observers != null) {
                    for (GameObserver o : observers)
                        o.onDefeatedPlayer(i);
                }
            }
        } else {
            throw new CommandExecuteException("[ERROR]: Player " + player.getID() + " has been defeated");
        }
    }

    /**
     * Updates the {@link #observers} giving them an instance of {@link GameDTO}.
     * This method is only used for the {@link view.console.GamePrinter}.
     */
    public void updateObservers() {
        GameDTO gameDTO = new GameDTO(players, level, currentPlayerNum);
        for (GameObserver o : observers)
            o.update(gameDTO);
    }

    /**
     * Notifies the observers in {@link #observers} that the game has finished.
     *
     * @param player the {@code Player} that has won the game.
     */
    private void endScreen(Player player) {
        for (GameObserver o : observers)
            o.endScreen(player.getDTO());
    }

    /**
     * Checks if a {@code Player} has been defeated.
     *
     * @param player the {@code Player} to check.
     * @return {@code true} if the player has lost, {@code false} otherwise.
     */
    public boolean hasLost(int player) {
        return players.get(player).hasLost();
    }

    /**
     * Gets the X dimension of the game's {@link #level}.
     *
     * @return an {@code int} representing the X dimension of the game's {@code Level}.
     */
    public int getDimX() {
        return this.level.getDimX();
    }

    /**
     * Given an {@code int} representing a player's index in the list of players, returns wether that player has placed all of its ships.
     *
     * @param player an {@code int} representing the {@code Player} to check.
     * @return {@code true} if the player has placed all of its ships, {@code false} otherwise.
     */
    public boolean allShipsPlaced(int player) {
        return players.get(player).allShipsPlaced();
    }

    /**
     * Gets the Y dimension of the game's {@link #level}.
     *
     * @return an {@code int} representing the Y dimension of the game's {@code Level}.
     */
    public int getDimY() {
        return this.level.getDimY();
    }

    /**
     * Adds a {@link GameObserver} to the list of observers.
     *
     * @param observer the {@code GameObserver} to be added.
     */
    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Makes a {@code Player} place his boats.
     *
     * @param player the {@code Player} that places the boats.
     * @return {@code false} if the player is real, {@code true} otherwise.
     */
    public boolean place(int player) {
        return players.get(player).place();
    }

    /**
     * Makes a {@code Player} attack.
     *
     * @param player the {@code Player} that will attack.
     * @return {@code false} if the player is real, {@code true} otherwise.
     */
    public boolean attack(int player) {
        return players.get(player).attack();
    }

    /**
     * Checks only one {@code Player} is still alive, meaning the game has finished.
     * If so, it calls the method {@link #endScreen(Player)} with the {@code winner} to notify the {@code observers}.
     *
     * @return {@code true} if the game has finished, {@code false} otherwise.
     */
    public boolean isFinished() {
        int end = 0;
        Player winner = null;
        for (Player p : players) {
            if (p.hasLost()) end++;
            else winner = p;
        }
        if (end == players.size() - 1) {
            endScreen(winner);
            return true;
        }
        return false;
    }

    /**
     * Returns a {@link JSONObject} instance containing the following values:
     * <ul>
     *     <li>{@code level}: a {@code String} indicating the game's level.</li>
     *     <li>{@code players}: a {@link JSONArray} with the information returned by the method {@link Player#getState()} of each player in {@link #players}.</li>
     *     <li>{@code currentPlayer}: an {@code int} that indicates the index of the {@link #currentPlayer} in the list of players.</li>
     * </ul>
     *
     * @return a {@code JSONObject} with said values.
     */
    private JSONObject getState() {
        JSONObject j = new JSONObject();
        j.put("level", level.toString());
        JSONArray ja = new JSONArray();
        for (Player p : players) {
            ja.put(p.getState());
        }
        j.put("players", ja);
        j.put("currentPlayer", currentPlayerNum);
        return j;
    }

    /**
     * Makes the {@link #currentPlayer} place his {@code Ship} represented by an {@code int} index and notifies the
     * {@code GameObservers} in {@link #observers}.
     *
     * @param ship an {@code int} that represents the ship to be placed.
     * @param pos the {@code Position} of the ship to be placed.
     * @param o the {@code Orientation} of the ship to be placed.
     * @throws CommandExecuteException if the ship cannot be placed correctly.
     */
    public void placeShip(int ship, Position pos, Orientation o) throws CommandExecuteException {
        currentPlayer.placeShip(ship, pos, o, this.level);
        for (GameObserver go : observers)
            go.onPlacedShip(currentPlayerNum, currentPlayer.getDTO());
    }

    /**
     * Makes the {@link #currentPlayer} place his {@code currentShip} and notifies the {@code GameObservers} in {@link #observers}.
     *
     * @param pos the {@code Position} of the ship to be placed.
     * @param o the {@code Orientation} of the ship to be placed.
     * @throws CommandExecuteException if the ship cannot be placed correctly.
     */
    public void placeShip(Position pos, Orientation o) throws CommandExecuteException {
        currentPlayer.placeShip(pos, o, level);
        if (observers != null) {
            for (GameObserver go : observers)
                go.onPlacedShip(currentPlayerNum, currentPlayer.getDTO());
        }
    }

    /**
     * Makes the {@link #currentPlayer} place his {@code Ships} and notifies the {@code GameObservers} in {@link #observers}.
     * No ships are placed if the {@code currentPlayer} is a real player.
     */
    public void placeCurrentPlayerShips() {
        currentPlayer.place();
    }

    /**
     * Initializes the game by setting its {@code Level}, the number of real players and their names and the number of bot players.
     * It also sets the {@link #currentPlayer} and the {@link #currentPlayerNum}.
     *
     * @param l the {@code Level} that will be set.
     * @param numRealPlayers an {@code int} indicating the number of real players of the game.
     * @param numBotPlayers an {@code int} indicating the number of bot players of the game.
     * @param names a {@code String} array containing the names of the real players.
     * @throws CommandExecuteException if two or more players share the same names.
     */
    public void init(Level l, int numRealPlayers, int numBotPlayers, String[] names) throws CommandExecuteException {
        for (int i = 0; i < numRealPlayers; ++i) addPlayer(new Player(names[i], l));
        for (int i = 0; i < numBotPlayers; ++i) addPlayer(new BotPlayer("Bot " + (i + 1), l, this));
        this.level = l;
        this.currentPlayer = players.get(0);
        this.currentPlayerNum = 0;
    }

    /**
     * Adds a {@code Player} to the list of players.
     *
     * @param p the {@code Player} to be added.
     * @throws CommandExecuteException if a {@code Player} with the same name already exists.
     */
    public void addPlayer(Player p) throws CommandExecuteException {
        for (Player player : players) {
            if (p.equals(player)) throw new CommandExecuteException("[ERROR]: Players must have different names");
        }
        players.add(p);
    }

    /**
     * This method returns the name of the {@code Player} at a certain index in the list of players.
     *
     * @param i an {@code int} indicating the index of the {@code Player}.
     * @return a {@code String} indicating the name of the {@code Player}.
     */
    public String getPlayerID(int i) {
        Player player = players.get(i);
        return player.getID() + " (" + (i + 1) + ")";
    }

    /**
     * Returns wether the {@code Player} at a certain index is the {@code currentPlayer}.
     *
     * @param i an {@code int} representing the index to check.
     * @return {@code true} if the {@code currentPlayer} has that index, {@code false} otherwise.
     */
    public boolean isCurrentPlayer(int i) {
        return players.get(i).equals(currentPlayer);
    }

    /**
     * Returns the number of players.
     *
     * @return an {@code int} representing the number of players.
     */
    public int getNumberOfPlayers() {
        return players.size();
    }

    /**
     * Given a certain {@code int} representing a {@code Player} and a {@code Position}, returns the result of calling {@link Player#checkMiss(Position)}.
     */
    public boolean checkMiss(int player, Position pos) {
        return players.get(player).checkMiss(pos);
    }

    /**
     * This method converts to a {@code String} the resulting {@link JSONObject} obtained from calling {@link #getState()}.
     *
     * @return the mentioned {@code String}.
     */
    public String toString() {
        return getState().toString();
    }

    /**
     * Given a file path represented by a {@code String}, this method saves the current {@code Game} to a file with
     * {@code .json} extension.
     *
     * @param file a {@code String} representing the file path.
     */
    public void save(String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file + ".json"))) { // try with resources lo cierra automaticamente
            writer.write(this.toString()); // Volcamos el serializado en el archivo
            System.out.println("Game successfully saved to file " + file + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (observers != null) {
            for (GameObserver go : observers)
                go.onSavedGame(file);
        }
    }

    /**
     * Given a {@link JSONObject} that represents a saved {@code Game}, this method tries to parse it and change all relevant attributes.
     *
     * @param jsonInput the {@code JSONObject} that contains the game's data.
     * @throws GameParseException if the {@code JSONObject} has incorrect format or its data is not correct.
     */
    public void load(JSONObject jsonInput) throws GameParseException {
        try {
            this.level = Level.Safeparse(jsonInput.getString("level"));
            if (level == null) throw new GameParseException();
            JSONArray playersJSON = jsonInput.getJSONArray("players");
            ArrayList<Player> players = new ArrayList<Player>();
            for (int i = 0; i < playersJSON.length(); i++)
                players.add(Player.parse(playersJSON.getJSONObject(i), level, this));
            this.players = players;
            try {
                this.currentPlayerNum = jsonInput.getInt("currentPlayer");
            } catch (NumberFormatException nfe) {
                throw new GameParseException();
            }
            if (currentPlayerNum < 0 || currentPlayerNum >= playersJSON.length()) throw new GameParseException();
            this.currentPlayer = players.get(currentPlayerNum);
        } catch (JSONException je) {
            throw new GameParseException();
        }
    }

    /**
     * Checks if a {@code Player} represented by an {@code int} is different from the {@code currentPlayer} and is still alive.
     *
     * @param p an {@code int} indicating the player.
     * @return {@code true} if the player is different from the {@code currentPlayer} and is still alive, {@code false} otherwise.
     */
    boolean checkPlayer(int p) {
        return ((players.get(p) != currentPlayer) && (!players.get(p).hasLost()));
    }

    /**
     * Notifies the {@code GameObservers} in {@link #observers} that the game phase has changed.
     */
    public void notifyPhaseChange() {
        for (GameObserver o : observers)
            o.onPhaseChange();
    }

    /**
     * Gets the index of the {@link #currentPlayer}.
     *
     * @return an {@code int} representing the index of the {@code currentPlayer} in the list of players.
     */
    public int getCurrentPlayer() {
        return currentPlayerNum;
    }

    /**
     * Given a {@code Player}, changes the game's {@link #currentPlayer}.
     *
     * @param player the {@code Player} that will be set.
     */
    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
        this.isFinished();
        if (observers != null) {
            for (GameObserver o : observers)
                o.onChangedPlayer(currentPlayerNum, currentPlayer.getReal());
        }
    }

    /**
     * Given a {@code Player} represented by an {@code int}, changes the game's {@link #currentPlayer}.
     *
     * @param player an {@code int} representing the index of the {@code Player} that will be set.
     */
    public void setCurrentPlayer(int player) {
        if (player >= players.size()) player = 0;
        currentPlayer = players.get(player);
        currentPlayerNum = player;
        this.isFinished();
        if (observers != null) {
            for (GameObserver o : observers) {
                o.onChangedPlayer(player, currentPlayer.getReal());
            }
        }
    }

    /**
     * Gets a {@link List} of {@link PlayerDTO} with the information of the game's players.
     *
     * @return a list of {@code PlayerDTO}.
     */
    public List<PlayerDTO> getPlayers() {
        List<PlayerDTO> playersList = new ArrayList<>();
        for (Player p : players) playersList.add(p.getDTO());
        return playersList;
    }

    /**
     * Checks if a {@code Position} is within bounds for the current {@code Game} and its {@code Level}.
     *
     * @param pos the {@code Position} to check.
     * @return {@code true} if the position is within bounds, {@code false} otherwise.
     */
    boolean checkPosition(Position pos) {
        return (0 <= pos.getX() && pos.getX() < level.getDimX() && 0 <= pos.getY() && pos.getY() < level.getDimY());
    }

    /**
     * Given the index of a {@code Ship}, this method sets that ship as {@code currentShip} for the {@link #currentPlayer}.
     *
     * @param i an {@code int} representing the index of the ship.
     */
    public void setCurrentShip(int i) {
        currentPlayer.setCurrentShip(i);
    }

    /**
     * Resets the game's {@link #players}.
     */
    public void reset() {
        players = new ArrayList<>();
    }
}