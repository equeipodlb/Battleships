package model;

import exceptions.GameParseException;
import org.json.JSONException;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import misc.Cell;

import java.util.List;


public class Player {
    Cell[][] observableBoard;
    protected boolean real = true; // indica si el jugador es una persona.
    private final String id;
    private Fleet fleet;
    private boolean lost = false;
    private int numberOfPlacedShips = 0;
    private boolean allShipsPlaced = false;

    public Player(String id, Level level) {
        this.id = id;
        fleet = new Fleet(level);
        observableBoard = new Cell[level.getDimX()][level.getDimY()];
        for (int i = 0; i < level.getDimX(); ++i) {
            for (int j = 0; j < level.getDimY(); ++j)
                observableBoard[i][j] = Cell.EMPTY;
        }
    }

    public Player(String id) {
        this.id = id;
    }

    /**
     * Checks if this {@code Player} is a real player or a bot.
     *
     * @return {@code true} if the player is real, {@code false} if it's a bot.
     */
    public boolean getReal() {
        return this.real;
    }

    /**
     * Gets this player's name.
     *
     * @return a {@code String} representing this player's name.
     */
    String getID() {
        return id;
    }

    /**
     * Makes this {@code Player} place his {@code Ship} represented by an {@code int} index and checks if the player has
     * been defeated.
     *
     * @param ship an {@code int} that represents the ship to be placed.
     * @param pos the {@code Position} of the ship to be placed.
     * @param o the {@code Orientation} of the ship to be placed.
     * @param level the {@code Level} of the game.
     * @throws CommandExecuteException if the ship cannot be placed correctly.
     */
    void placeShip(int ship, Position pos, Orientation o, Level level) throws CommandExecuteException {
        fleet.placeShip(ship, pos, o, level);
        numberOfPlacedShips++;
        allShipsPlaced = numberOfPlacedShips == level.getNumberOfShips();
    }

    /**
     * Makes this {@code Player} place his {@code currentShip} and checks if the player has been defeated.
     *
     * @param pos the {@code Position} of the ship to be placed.
     * @param o the {@code Orientation} of the ship to be placed.
     * @param level the {@code Level} of the game.
     * @throws CommandExecuteException if the ship cannot be placed correctly.
     */
    void placeShip(Position pos, Orientation o, Level level) throws CommandExecuteException {
        fleet.placeShip(pos, o, level);
        numberOfPlacedShips++;
        allShipsPlaced = numberOfPlacedShips == level.getNumberOfShips();
    }

    /**
     * Attempts to attack this player's ship. It also checks if the player has lost.
     *
     * @param pos the {@code Position} that will be attacked.
     * @param level the {@code Game} level.
     * @return {@code true} if any {@code Ship} has been sunk, {@code false} otherwise.
     * @throws CommandExecuteException if the attacked position is not valid.
     */
    boolean attackShip(Position pos, Level level) throws CommandExecuteException {
        if (!fleet.checkPosition(pos, level)) {
            throw new CommandExecuteException("[ERROR]: Out of bounds position");
        } else if (checkMissOrHit(pos)) {
            throw new CommandExecuteException("[ERROR]: Player " + getID() + " has already received a shot in that position");
        } else {
            List<Boolean> l = this.receiveShot(pos);
            if (observableBoard[pos.getX()][pos.getY()] == Cell.HIT) { // si se ha acertado;
                if (fleet.checkFleetDefeat()) lost = true;
            }
            return l.get(1);
        }
    }

    /**
     * This method overwrites the equality comparator by comparing two players' names.
     *
     * @param player the player that will be compared.
     * @return {@code true} if the two players have the same name, {@code false} otherwise.
     */
    boolean equals(Player player) {
        return this.id.equals(player.id);
    }

    /**
     * Makes this {@code Player} place his boats. This method is overwritten by {@link BotPlayer#place()} if the player is a bot.
     *
     * @return {@code false} if the player is real, {@code true} otherwise.
     */
    boolean place() {
        return false;
    }

    /**
     * Makes this {@code Player} attack. This method is overwritten by {@link BotPlayer#attack()} if the player is a bot.
     *
     * @return {@code false} if the player is real, {@code true} otherwise.
     */
    boolean attack() {
        return false;
    }

    /**
     * Checks if this {@code Player} has lost.
     *
     * @return {@code true} if the player has lost, {@code false} otherwise.
     */
    boolean hasLost() {
        return lost;
    }

    /**
     * Checks if this {@code Player} has placed his ships.
     *
     * @return {@code true} if the player has placed his ships, {@code false} otherwise.
     */
    public boolean allShipsPlaced() {
        return this.allShipsPlaced;
    }

    /**
     * Registers a shot at the given {@code Position} and changes the observable board used by instances
     * of {@link BotPlayer} to perform their strategies.
     *
     * @param pos the position where the shot will be registered.
     * @return a {@link List<>} containing exactly two {@code Boolean} values indicating if a {@code Ship} has been hit and if it has
     * been sunk, respectively.
     */
    List<Boolean> receiveShot(Position pos) {
        List<Boolean> l = fleet.receiveShot(pos);
        if (l.get(0)) observableBoard[pos.getX()][pos.getY()] = Cell.HIT;
        else observableBoard[pos.getX()][pos.getY()] = Cell.MISS;
        return l;
    }

    /**
     * Checks if a given {@code Position} is contained in the set of missed attacks upon this player.
     *
     * @param p the position to check.
     * @return wether the position is contained in said set.
     */
    boolean checkMiss(Position p) {
        return fleet.checkMiss(p);
    }

    /**
     * Checks if a given {@code Position} is contained in the set of hit attacks upon this player.
     *
     * @param p the position to check.
     * @return wether the position is contained in said set.
     */
    boolean checkHit(Position p) {
        return fleet.checkHit(p);
    }

    /**
     * Checks if a given {@code Position} is eiter in the set of hit attacks or in the set of missed attacks upon this player.
     *
     * @param p the position to check.
     * @return wether the position is contained in any of said sets.
     */
    boolean checkMissOrHit(Position p) {
        return checkMiss(p) || checkHit(p);
    }

    // Método que se usa para tests de JUnit
    public void setLost(boolean b) {
        this.lost = b;
    }

    /**
     * Returns a {@link JSONObject} instance containing the following values:
     * <ul>
     *     <li>{@code name}: a {@code String} indicating the player's name.</li>
     *     <li>{@code real}: a {@code Boolean} value that indicates if the player is real.</li>
     *     <li>{@code data}: the {@code JSONObject} returned by {@link Fleet#getState()}.</li>
     * </ul>
     *
     * @return a {@code JSONObject} with said values.
     */
    public JSONObject getState() {
        JSONObject j = new JSONObject();
        j.put("name", this.id);
        j.put("real", this.real);
        j.put("data", this.fleet.getState());
        return j;
    }

    /**
     * Given the information contained in a {@link JSONObject}, this method creates an instance of {@code Player}.
     *
     * @param info the {@code JSONObject} which contains the data.
     * @param l the {@code Level} that will be used to check if data is correct.
     * @param game the {@code Game} instance used to create the {@code BotPlayer} if the player is not real.
     * @return the instance of {@code Player} with its attributes set according to the provided data.
     * @throws GameParseException if the provided data contains incorrect or inconsistent values.
     * @throws JSONException if the {@code JSONObject} doesn't contain all needed keys.
     */
    public static Player parse(JSONObject info, Level l, Game game) throws GameParseException, JSONException {
        Player p;
		JSONObject ja = info.getJSONObject("data"); // Obtenemos la pos coordenada a coordenada
        if (info.getBoolean("real")) p = new Player(info.getString("name"),l);
        else p = new BotPlayer(info.getString("name"), l, game);
		if (ja.getJSONArray("fleet").length() != l.getNumberOfShips()) throw new GameParseException();
        p.fleet = Fleet.parse(ja, l);
        p.allShipsPlaced = p.fleet.checkAllShipsPlaced();
		return p;
	}

    /**
     * Creates a {@link PlayerDTO} instance which has the same attributes as this instance of {@code Player}.
     *
     * @return a {@code PlayerDTO} created from this player.
     */
    public PlayerDTO getDTO() {
        return new PlayerDTO(id, real, lost, fleet, allShipsPlaced);
    }

    /**
     * Sets this players' fleet {@code curretShip} to be {@code i}.
     *
     * @param i an {@code int} representing the index of the ship in the fleet.
     */
    void setCurrentShip(int i) {
        fleet.setCurrentShip(i);
    }

    /**
     * Checks if this {@code Player} has been defeated.
     *
     * @return {@code true} if the player has been defeated, {@code false} otherwise.
     */
    boolean hasBeenDefeated() {
        return fleet.checkFleetDefeat();
    }
}