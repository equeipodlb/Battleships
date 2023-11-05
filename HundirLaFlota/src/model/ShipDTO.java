package model;

import misc.Cell;
import org.json.JSONArray;
import org.json.JSONObject;

import misc.Orientation;
import misc.Position;

public class ShipDTO {
    private final int length;
    private final Orientation orientation;
    private final Position position;
    private final boolean sunk;
    private final boolean placed;
    private final boolean[] hits;

    public ShipDTO(int length, boolean placed, boolean sunk, boolean[] hits, Position pos, Orientation o) {
        this.placed = placed;
        this.sunk = sunk;
        this.length = length;
        this.hits = hits.clone();
        this.position = pos != null ? new Position(pos.getX(), pos.getY()) : null;
        this.orientation = o;
    }

    public int getLength() {
        return this.length;
    }

    public boolean getSunk() {
        return this.sunk;
    }

    public boolean isPlaced() {
        return this.placed;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public boolean getHit(int i) {
        return hits[i];
    }

    public JSONObject getState() {
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

    public Cell getPosition(Position pos) {
        Cell cell = Cell.EMPTY;
        if (orientation == Orientation.HORIZONTAL) {
            if (pos.getY() == position.getY() && pos.getX() - position.getX() < length && pos.getX() - position.getX() >= 0) {
                if (hits[pos.getX() - position.getX()])
                    cell = Cell.HIT;
                else cell = Cell.SHIP;
            }
        } else if (orientation == Orientation.VERTICAL) {
            if (pos.getX() == position.getX() && pos.getY() - position.getY() < length && pos.getY() - position.getY() >= 0) {
                if (hits[pos.getY() - position.getY()])
                    cell = Cell.HIT;
                else cell = Cell.SHIP;
            }
        }
        return cell;
    }
}
