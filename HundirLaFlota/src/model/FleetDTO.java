package model;

import misc.Cell;
import misc.Orientation;
import misc.Position;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FleetDTO {
    private final ArrayList<ShipDTO> fleet;
    private final ShipDTO currentShip;
    private final List<Position> misses;
    private final List<Position> hits;
    private final List<Position> occupied;

    public FleetDTO(List<Ship> ships, int currentShipIndex, List<Position> misses, List<Position> hits, List<Position> occupied) {
        this.fleet = new ArrayList<>();
        for (Ship ship : ships) this.fleet.add(ship.getDTO());
        currentShip = this.fleet.get(currentShipIndex);
        this.misses = new ArrayList<>();
        for (Position miss : misses) this.misses.add(new Position(miss.getX(), miss.getY()));
        this.hits = new ArrayList<>();
        for (Position hit : hits) this.hits.add(new Position(hit.getX(), hit.getY()));
        this.occupied = new ArrayList<>();
        for (Position pos : occupied) this.occupied.add(new Position(pos.getX(), pos.getY()));
    }

    public ShipDTO getCurrentShip() {
        return currentShip;
    }

    public Cell checkPos(int x, int y) {
        for (Position p : misses) if (p.getX() == x && p.getY() == y) return Cell.MISS;
        for (ShipDTO ship : fleet) {
            if (ship.isPlaced()) {
                for (int j = 0; j < ship.getLength(); ++j) {
                    if (ship.getOrientation() == Orientation.HORIZONTAL && y == ship.getY())
                        if (ship.getX() + j == x) {
                            if (ship.getHit(j)) return Cell.HIT.setLength(ship.getLength()).setShipPos(j)
                                    .setOrientation(ship.getOrientation()).setHit(true).setSunk(ship.getSunk());
                            else return Cell.SHIP.setLength(ship.getLength()).setShipPos(j)
                                    .setOrientation(ship.getOrientation()).setHit(false).setSunk(ship.getSunk());
                        }
                    if (ship.getOrientation() == Orientation.VERTICAL && x == ship.getX()) {
                        if (ship.getY() + j == y) {
                            if (ship.getHit(j)) return Cell.HIT.setLength(ship.getLength()).setShipPos(j)
                                    .setOrientation(ship.getOrientation()).setHit(true).setSunk(ship.getSunk());
                            else return Cell.SHIP.setLength(ship.getLength()).setShipPos(j)
                                    .setOrientation(ship.getOrientation()).setHit(false).setSunk(ship.getSunk());
                        }
                    }
                }
            }
        }
        return Cell.EMPTY;
    }

    public JSONObject getState() {
        JSONObject j = new JSONObject();

        JSONArray ja = new JSONArray();
        for (ShipDTO barco : fleet) {
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

    public String getPositionToString(Position position) {
        for (ShipDTO s : fleet) {
            Cell c = s.getPosition(position);
            if (c == Cell.HIT)
                return "*";
            else if (c == Cell.SHIP)
                return "0";
        }
        return " ";
    }

    public boolean checkMiss(Position position) {
        for (Position miss : misses)
            if (position.equals(miss))
                return true;
        return false;
    }
}
