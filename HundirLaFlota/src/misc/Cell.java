package misc;

public enum Cell {
    HIT, MISS, EMPTY, SHIP;

    // Atributos que se utilizan para determinar las propiedades visuales de una barco en la GUI.
    int length;
    Orientation orientation;
    int shipPos;
    boolean hit;
    boolean sunk;

    public int getLength() {
        return length;
    }

    public Cell setLength(int length) {
        this.length = length;
        return this;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Cell setOrientation(Orientation o) {
        this.orientation = o;
        return this;
    }

    public int getShipPos() {
        return shipPos;
    }

    public Cell setShipPos(int shipPos) {
        this.shipPos = shipPos;
        return this;
    }

    public boolean isHit() {
        return hit;
    }

    public Cell setHit(boolean hit) {
        this.hit = hit;
        return this;
    }

    public boolean isSunk() {
        return sunk;
    }

    public Cell setSunk(boolean sunk) {
        this.sunk = sunk;
        return this;
    }
}
