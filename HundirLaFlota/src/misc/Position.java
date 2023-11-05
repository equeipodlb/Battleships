package misc;

import exceptions.GameParseException;
import org.json.JSONArray;
import org.json.JSONException;

public class Position {
    int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position parse(JSONArray jsonArray) throws GameParseException, JSONException {
        if (jsonArray.length() != 2) throw new GameParseException();
        int x = jsonArray.getInt(0), y = jsonArray.getInt(1);
        return new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Position p) {
        return this.x == p.getX() && this.y == p.getY();
    }

    public JSONArray getState() {
        JSONArray ja = new JSONArray();
        ja.put(x);
        ja.put(y);
        return ja;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
