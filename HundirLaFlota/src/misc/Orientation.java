package misc;

import exceptions.OrientationFormatException;

public enum Orientation {
    VERTICAL("v"), HORIZONTAL("h");
    private final String shortcut;

    Orientation(String name) {
        this.shortcut = name;
    }

    public static Orientation parse(String str) throws OrientationFormatException {
        for (Orientation orientation : Orientation.values())
            if (orientation.getShortcut().equalsIgnoreCase(str)) return orientation;
        throw new OrientationFormatException();
    }

    public boolean sameOrientation(Orientation o) {
        return this.equals(o);
    }

    public String getShortcut() {
        return shortcut;
    }
}
