package misc;

import exceptions.CommandParseException;

public enum Level {
    EASY("easy", 3, 5, 5, new int[]{1, 2, 3}),
    HARD("hard", 4, 7, 7, new int[]{1, 2, 3, 4}),
    INSANE("insane", 5, 8, 8, new int[]{1, 2, 3, 4, 5});

    private final String name;
    private final int numberOfShips;
    private final int dim_x;
    private final int dim_y;
    private final int[] lengths;

    Level(String name, int numberOfShips, int dim_x, int dim_y, int[] length) {
        this.name = name;
        this.numberOfShips = numberOfShips;
        this.dim_x = dim_x;
        this.dim_y = dim_y;
        this.lengths = length;
    }

    public static Level parse(String inputString) throws CommandParseException {
        for (Level level : Level.values())
            if (level.name().equalsIgnoreCase(inputString))
                return level;
        throw new CommandParseException("[ERROR]: Incorrect format for level");
    }

    public static Level Safeparse(String inputString) {
        Level l = null;
        for (Level level : Level.values())
            if (level.name().equalsIgnoreCase(inputString))
                l = level;
        return l;
    }

    public int getNumberOfShips() {
        return numberOfShips;
    }

    public int getDimX() {
        return dim_x;
    }

    public int getDimY() {
        return dim_y;
    }

    public int getShipLength(int i) {
        return lengths[i];
    }

    public String getName() {
        return this.name;
    }
}
