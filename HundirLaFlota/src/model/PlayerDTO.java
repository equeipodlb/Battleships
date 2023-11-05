package model;

import misc.Position;
import org.json.JSONObject;

import misc.Cell;

public class PlayerDTO {
    private final String id;
    private final boolean real;
    private final FleetDTO fleet;
    private final boolean lost;
    private final boolean allShipsPlaced;

    public PlayerDTO(String id, boolean real, boolean lost, Fleet fleet, boolean allShipsPlaced) {
        this.id = id;
        this.real = real;
        this.lost = lost;
        this.fleet = fleet.getDTO();
        this.allShipsPlaced = allShipsPlaced;
    }

    boolean equals(PlayerDTO player) {
        return this.id.equals(player.id);
    }

    public boolean isReal() {
        return real;
    }

    public String getID() {
        return id;
    }

    public boolean hasLost() {
        return lost;
    }

    public Cell getCell(int x, int y) {
        return fleet.checkPos(x, y);
    }

    public boolean getReal() {
        return this.real;
    }

    public boolean getAllShipsPlaced() {
        return allShipsPlaced;
    }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();
        j.put("name", this.id);
        j.put("real", this.real);
        j.put("allShipsPlaced", this.allShipsPlaced);
        j.put("data", this.fleet.getState());
        return j;
    }

    public String getPositionToString(Position position) {
        return this.fleet.getPositionToString(position);
    }

    public boolean checkMiss(Position position) {
        return fleet.checkMiss(position);
    }
}
