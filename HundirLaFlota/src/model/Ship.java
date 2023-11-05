package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameParseException;
import misc.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import misc.Cell;
import misc.Orientation;
import misc.Position;

public class Ship {
    private final int length;
    boolean sunk, placed;
    private Orientation orientation;
    private Position position;
    private boolean[] hits;

    Ship(int length) {
        this.placed = false;
        this.sunk = false;
        this.length = length;
        this.hits = new boolean[length];
        this.position = new Position(-1, -1);
    }

    /**
     * Given the information contained in a {@link JSONObject}, this method creates an instance of {@code Ship}.
     *
     * @param object the {@code JSONObject} which contains the data.
     * @param level  the {@code Level} that will be used to check if data is correct.
     * @return the instance of {@code Ship} with its attributes set according to the provided data.
     * @throws GameParseException if the provided data contains incorrect or inconsistent values.
     * @throws JSONException      if the {@code JSONObject} doesn't contain all needed keys.
     */
    public static Ship parse(JSONObject object, Level level) throws GameParseException, JSONException {
        int length = object.getInt("length");
        Ship s = new Ship(length);
        int o = object.getInt("orientation");
        if (o == 0) s.orientation = Orientation.VERTICAL;
        else if (o == 1) s.orientation = Orientation.HORIZONTAL;
        else throw new GameParseException();
        s.placed = object.getBoolean("placed");
        s.sunk = object.getBoolean("sunk");
        if (!s.placed && s.sunk) throw new GameParseException();
        s.position = Position.parse(object.getJSONArray("pos"));
        if (!s.placed && (s.position.getX() != -1 || s.position.getY() != -1)) throw new GameParseException();
        if (s.placed && (s.position.getX() < 0 || s.position.getX() >= level.getDimX() || s.position.getY() < 0 || s.position.getY() >= level.getDimY()))
            throw new GameParseException();
        JSONArray ja = object.getJSONArray("hits");
        if (length != ja.length()) throw new GameParseException();
        boolean[] hits = new boolean[length];
        for (int i = 0; i < ja.length(); ++i) {
            hits[i] = ja.getBoolean(i);
            if (s.sunk && !hits[i]) throw new GameParseException();
        }
        s.hits = hits;
        return s;
    }

    /**
     * Gets the {@code length} of the {@code Ship}.
     *
     * @return an {@code int} representing the ship's length.
     */
    int getLength() {
        return this.length;
    }

    /**
     * Returns if the ship is sunk.
     *
     * @return {@code true} if the ship is sunk, {@code false} otherwise.
     */
    boolean getSunk() {
        return this.sunk;
    }

    /**
     * Checks if the ship is sunk. If so, it changes its attribute {@link #sunk}.
     *
     * @return {@code true} if the ship is sunk, {@code false} otherwise.
     */
    private boolean checkSunk() {
        boolean hit = true;
        for (int i = 0; i < hits.length && hit; ++i) {
            hit = hits[i];
        }
        if (hit) {
            sunk = true;
        }
        return hit;
    }

    /**
     * Sets this ship as placed by changing its relevant attributes.
     *
     * @param pos the ship's {@code Position}.
     * @param o   the ship's {@code Orientation}
     */
    void placeShip(Position pos, Orientation o) {
        this.placed = true;
        this.position = pos;
        this.orientation = o;
    }

    /**
     * Checks if a shot at a certain {@code Position} would hit this ship.
     *
     * @param pos the {@code Position} to check.
     * @return a {@link List} containing two {@code Boolean} values indicating if the ship has been hit and if it has been sunk.
     */
    List<Boolean> receiveShot(Position pos) {
        boolean shot = false;
        boolean sunk = false;
        List<Boolean> l = new ArrayList();
        if (orientation == Orientation.HORIZONTAL) {
            if (pos.getY() == position.getY() && pos.getX() - position.getX() < length && pos.getX() - position.getX() >= 0) {
                shot = true;
                hits[Math.abs(pos.getX() - position.getX())] = true;
                sunk = checkSunk();
            }
        } else if (orientation == Orientation.VERTICAL) {
            if (pos.getX() == position.getX() && pos.getY() - position.getY() < length && pos.getY() - position.getY() >= 0) {
                shot = true;
                hits[Math.abs(pos.getY() - position.getY())] = true;
                sunk = checkSunk();
            }
        }
        l.add(shot);
        l.add(sunk);
        return l;
    }

    /**
     * Returns a {@link JSONObject} instance containing the following values:
     * <ul>
     *     <li>{@code pos}: a {@link JSONArray} with the information returned by the method {@link Position#getState()}.</li>
     *     <li>{@code orientation}: an {@code int} which is either 0 or 1 indicating the ship's orientation. 0 means
     *     {@code HORIZONTAL} and 1 means {@code VERTICAL}.</li>
     *     <li>{@code sunk}: a {@code Boolean} that indicates wether the ship is sunk.</li>
     *     <li>{@code placed}: a {@code Boolean} that indicates wether the ship is placed.</li>
     *     <li>{@code length}: an {@code int} that represents the ship's length.</li>
     *     <li>{@code hits}: a {@link JSONArray} of {@code Boolean} values where each value represents a ship's position and takes value 1
     *     if ship was hit there and value 0 otherwise.</li>
     * </ul>
     *
     * @return a {@code JSONObject} with said values.
     */
    JSONObject getState() {
        JSONObject jo = new JSONObject();
        jo.put("pos", this.position.getState());
        int orient = this.orientation == Orientation.VERTICAL ? 0 : 1; // 0 si es vertical 1 si es horizontal
        jo.put("orientation", orient);
        jo.put("sunk", this.sunk);
        jo.put("placed", this.placed);
        jo.put("length", this.length);
        JSONArray hits = new JSONArray();
        for (boolean hit : this.hits) {
            hits.put(hit);
        }
        jo.put("hits", hits);
        return jo;
    }

    /**
     * Creates a {@link ShipDTO} instance which has the same attributes as this instance of {@code Ship}.
     *
     * @return a {@code ShipDTO} created from this ship.
     */
    public ShipDTO getDTO() {
        return new ShipDTO(length, placed, sunk, hits, position, orientation);
    }
}
