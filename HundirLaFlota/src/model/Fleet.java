package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import exceptions.GameParseException;
import org.json.JSONArray;

import org.json.JSONException;

import org.json.JSONObject;

import exceptions.CommandExecuteException;
import misc.Cell;
import misc.Level;
import misc.Orientation;
import misc.Position;

public class Fleet {
    private ArrayList<Ship> fleet;
    private Ship currentShip;
    private int currentIndex;
    private List<Position> misses;
    private List<Position> hits;
    private List<Position> occupied;

    Fleet() {
        this.occupied = null;
        this.fleet = null;
        this.hits = null;
        this.misses = null;
    }

    Fleet(Level level) {
        fleet = new ArrayList<Ship>();
        misses = new ArrayList<Position>();
        hits = new ArrayList<Position>();
        occupied = new ArrayList<Position>();
        for (int i = 0; i < level.getNumberOfShips(); ++i) {
            fleet.add(new Ship(level.getShipLength(i)));
        }
        currentShip = fleet.get(0);
        currentIndex = 0;
    }

    /**
     * Given the information contained in a {@link JSONObject}, this method creates an instance of {@code Fleet}.
     *
     * @param info the {@code JSONObject} which contains the data.
     * @param level the {@code Level} that will be used to check if data is correct.
     * @return the instance of {@code Fleet} with its attributes set according to the provided data.
     * @throws GameParseException if the provided data contains incorrect or inconsistent values.
     * @throws JSONException if the {@code JSONObject} doesn't contain all needed keys.
     */
    static Fleet parse(JSONObject info, Level level) throws GameParseException, JSONException {
        Fleet f = new Fleet();
        JSONArray ja = info.getJSONArray("fleet");

        ArrayList<Ship> ships = new ArrayList<Ship>();
        for (int i = 0; i < ja.length(); ++i) {
            Ship ship = Ship.parse(ja.getJSONObject(i), level);
            ships.add(ship);
        }
        f.fleet = ships;

        JSONArray ja1 = info.getJSONArray("misses");
        ArrayList<Position> misses = new ArrayList<Position>();
        for (int i = 0; i < ja1.length(); ++i) {
            Position pos = Position.parse(ja1.getJSONArray(i));
            if (pos.getX() < 0 || pos.getX() >= level.getDimX() || pos.getY() < 0 || pos.getY() >= level.getDimY())
                throw new GameParseException();
            misses.add(pos);
        }
        f.misses = misses;

        JSONArray ja2 = info.getJSONArray("hits");
        ArrayList<Position> hits = new ArrayList<Position>();
        for (int i = 0; i < ja2.length(); ++i) {
            Position pos = Position.parse(ja2.getJSONArray(i));
            if (pos.getX() < 0 || pos.getX() >= level.getDimX() || pos.getY() < 0 || pos.getY() >= level.getDimY())
                throw new GameParseException();
            hits.add(pos);
        }
        f.hits = hits;

        JSONArray ja3 = info.getJSONArray("occupied");
        ArrayList<Position> occupied = new ArrayList<Position>();
        for (int i = 0; i < ja3.length(); ++i) {
            Position pos = Position.parse(ja3.getJSONArray(i));
            if (pos.getX() < 0 || pos.getX() >= level.getDimX() || pos.getY() < 0 || pos.getY() >= level.getDimY())
                throw new GameParseException();
            occupied.add(pos);
        }
        f.occupied = occupied;

        return f;
    }

    /**
     * Places a {@code Ship} after checking that the {@code Position} is correct.
     *
     * @param i an {@code int} that represents the index of the ship.
     * @param pos the {@code Position} of the ship to be placed.
     * @param o the {@code Orientation} of the ship to be placed.
     * @param level the {@code Level} needed to place the ship.
     * @throws CommandExecuteException if any of the methods called throw this exception.
     */
    void placeShip(int i, Position pos, Orientation o, Level level) throws CommandExecuteException {
        checkShip(pos, o, level);
        checkOtherShips(pos, o, level);
        fleet.get(i).placeShip(pos, o);
    }

    /**
     * This method places a {@code Ship} similarly to {@link #placeShip(int, Position, Orientation, Level)}. In this case,
     * the placed ship is the {@link #currentShip}.
     */
    void placeShip(Position pos, Orientation o, Level level) throws CommandExecuteException {
        checkShip(pos, o, level);
        checkOtherShips(pos, o, level);
        currentShip.placeShip(pos, o);
        updateCurrentShip();
    }

    /**
     * Checks if the {@link #currentShip} could be placed at a certain {@code Position} with a certain {@code Orientation}
     * without getting out of bounds.
     * If it is not possible, a {@link CommandExecuteException} is thrown.
     *
     * @param pos the {@code Position} where the ship would be placed.
     * @param o the {@code Orientation} that the ship would have.
     * @param level the {@code Level} used to check if the ship is in bounds.
     * @throws CommandExecuteException if the ship could not be placed.
     */
    private void checkShip(Position pos, Orientation o, Level level) throws CommandExecuteException {
        int dimX = level.getDimX(), dimY = level.getDimY(), iniX = pos.getX(), iniY = pos.getY(), fin;
        if (o.sameOrientation(Orientation.HORIZONTAL)) {
            fin = iniX + currentShip.getLength() - 1;
            if (fin < 0 || fin >= dimX)
                throw new CommandExecuteException("[ERROR]: Ship out of bounds");
        } else {
            fin = iniY + currentShip.getLength() - 1;
            if (fin < 0 || fin >= dimY)
                throw new CommandExecuteException("[ERROR]: Ship out of bounds");
        }
    }

    /**
     * Checks if the {@link #currentShip} could be placed at a certain {@code Position} with a certain {@code Orientation}
     * without having any adjacent ships.
     * If it is not possible, a {@link CommandExecuteException} is thrown.
     *
     * @param pos the {@code Position} where the ship would be placed.
     * @param o the {@code Orientation} that the ship would have.
     * @param level the {@code Level} used to check if the ship is in bounds.
     * @throws CommandExecuteException if the ship could not be placed.
     */
    private void checkOtherShips(Position pos, Orientation o, Level level) throws CommandExecuteException {
        boolean canBePlaced = true;
        if (!occupied.isEmpty()) {
            for (int i = 0; i < currentShip.getLength() && canBePlaced; ++i) {
                Position auxPosition;
                if (o.sameOrientation(Orientation.HORIZONTAL)) {
                    auxPosition = new Position(pos.getX() + i, pos.getY());
                } else
                    auxPosition = new Position(pos.getX(), pos.getY() + i);

                if (checkPosition(auxPosition, level)) {
                    for (Position p : occupied) {
                        if (auxPosition.equals(p)) {
                            canBePlaced = false;
                            throw new CommandExecuteException(
                                    "[ERROR]: Invalid position for ship. Two ships cannot be adjacent");
                        }
                    }
                } else {
                    canBePlaced = false;
                    throw new CommandExecuteException("[ERROR]: Ship out of bounds");
                }
            }
        }
        if (canBePlaced) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= currentShip.getLength(); ++j) {
                    Position auxPosition;
                    if (o.sameOrientation(Orientation.HORIZONTAL))
                        auxPosition = new Position(pos.getX() + j, pos.getY() + i);
                    else
                        auxPosition = new Position(pos.getX() + i, pos.getY() + j);
                    if (checkPosition(auxPosition, level))
                        occupied.add(auxPosition);
                }
            }
        }
    }

    /**
     * Checks if a {@code Position} is in bounds for a board defined by {@code Level}.
     *
     * @param pos the {@code Position} to check.
     * @param level the {@code Level} where the position is checked.
     * @return {@code true} if the position if in bounds, {@code false} otherwise.
     */
    boolean checkPosition(Position pos, Level level) {
        int dimX = level.getDimX(), dimY = level.getDimY(), iniX = pos.getX(), iniY = pos.getY();
        return iniX >= 0 && iniX < dimX && iniY >= 0 && iniY < dimY;
    }

    /**
     * Checks if a shot in a certain {@code Position} would hit any {@code Ship}. If so, it also returns if the ship is sunk.
     *
     * @param pos the {@code Position} to check.
     * @return a {@link List} containing two {@code Boolean} values indicating if any {@code Ship} was hit and if it was sunk.
     */
    private List<Boolean> checkShot(Position pos) {
        List<Boolean> l = new ArrayList<Boolean>();
        boolean sunk = false;
        boolean hit = false;
        for (Ship s : fleet) {
            List<Boolean> aux = s.receiveShot(pos);
            if (aux.get(0)) {
                hit = true;
                sunk = aux.get(1);
            }
        }
        l.add(hit);
        l.add(sunk);
        return l;
    }

    /**
     * Checks if a shot at a certain {@code Position} would hit a {@code Ship} making use of {@link #checkShot(Position)}. It then adds
     * the position to the {@link #misses} list or to the {@link #hits} list, depending on the check.
     *
     * @param pos the {@code Position} to check.
     * @return a {@link List} containing two {@code Boolean} values indicating if any {@code Ship} was hit and if it was sunk.
     */
    List<Boolean> receiveShot(Position pos) {
        List<Boolean> l = checkShot(pos);
        if (!l.get(0))
            misses.add(pos);
        else {
            hits.add(pos);
        }
        return l;
    }

    /**
     * Checks if a {@code Position} is in the list of {@link #misses}.
     *
     * @param p the {@code Position} to check.
     * @return {@code true} if the position is in the list, {@code false} otherwise.
     */
    boolean checkMiss(Position p) {
        for (Position miss : misses)
            if (p.equals(miss))
                return true;
        return false;
    }

    /**
     * Checks if a {@code Position} is in the list of {@link #hits}.
     *
     * @param p the {@code Position} to check.
     * @return {@code true} if the position is in the list, {@code false} otherwise.
     */
    boolean checkHit(Position p) {
        for (Position hit : hits)
            if (p.equals(hit))
                return true;
        return false;
    }

    /**
     * Checks if all ships in this {@code Fleet} have been sunk.
     *
     * @return {@code true} if all ships have been sunk, {@code false} otherwise.
     */
    boolean checkFleetDefeat() {
        boolean defeat = true;
        for (Ship s : fleet) {
            if (!s.getSunk())
                defeat = false;
        }
        return defeat;
    }

    /**
     * Changes both the {@link #currentIndex} and the {@link #currentShip} to the next one in the list of ships.
     */
    private void updateCurrentShip() {
        if (this.currentIndex + 1 < fleet.size()) {
            this.currentIndex++;
            this.currentShip = fleet.get(this.currentIndex);
        }
    }

    /**
     * Returns a {@link JSONObject} instance containing the following values:
     * <ul>
     *     <li>{@code fleet}: a {@link JSONArray} with the information returned by the method {@link Ship#getState()} of each ship in {@link #fleet}.</li>
     *     <li>{@code misses}: a {@link JSONArray} with the information returned by the method {@link Position#getState()} of each position in {@link #misses}.</li>
     *     <li>{@code hits}: a {@link JSONArray} with the information returned by the method {@link Position#getState()} of each position in {@link #hits}.</li>
     *     <li>{@code occupied}: a {@link JSONArray} with the information returned by the method {@link Position#getState()} of each position in {@link #occupied}.</li>
     * </ul>
     *
     * @return a {@code JSONObject} with said values.
     */
    JSONObject getState() {
        JSONObject j = new JSONObject();
        JSONArray ja = new JSONArray();
        for (Ship barco : fleet) {
            ja.put(barco.getState());
        }
        JSONArray ja1 = new JSONArray();
        for (Position p : misses) {
            ja1.put(p.getState());
        }
        JSONArray ja2 = new JSONArray();
        for (Position p : hits) {
            ja2.put(p.getState());

        }
        JSONArray ja3 = new JSONArray();
        for (Position p : occupied) {
            ja3.put(p.getState());
        }
        j.put("fleet", ja);
        j.put("misses", ja1);
        j.put("hits", ja2);
        j.put("occupied", ja3);
        return j;
    }

    /**
     * Creates a {@link FleetDTO} instance which has the same attributes as this instance of {@code Fleet}.
     *
     * @return a {@code FleetDTO} created from this fleet.
     */
    FleetDTO getDTO() {
        return new FleetDTO(fleet, currentIndex, misses, hits, occupied);
    }

    /**
     * Changes the {@link #currentShip} and the {@link #currentIndex} to that of {@code i}.
     *
     * @param i an {@code int} that represents the index that will be set.
     */
    void setCurrentShip(int i) {
        currentShip = fleet.get(i);
        currentIndex = i;
    }

    /**
     * Checks if all {@code Ships} have been placed.
     * @return {@code true} if all ships in this {@code Fleet} have been placed, {@code false} otherwise.
     */
    boolean checkAllShipsPlaced() {
        boolean allShipsPlaced = true;
        for (Ship s : this.fleet)
            allShipsPlaced = allShipsPlaced && s.placed;
        return allShipsPlaced;
    }
}
