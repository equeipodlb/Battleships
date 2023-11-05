package controller.commands;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import exceptions.OrientationFormatException;
import misc.Orientation;
import misc.Position;
import model.Game;

class AddCommand extends Command {
    private static final String NAME = "add";
    private static final String SHORTCUT = "a";
    private static final String HELP = "add a ship in position x, y";
    private static final String DETAILS = "[a]dd <x> <y> <o>";
    private int x, y;
    private Orientation orientation;

    public AddCommand() {
        super(NAME, SHORTCUT, DETAILS, HELP);
    }

    public AddCommand(int num1, int num2, Orientation o) {
        super(NAME, SHORTCUT, DETAILS, HELP);
        this.x = num1;
        this.y = num2;
        this.orientation = o;
    }

    public boolean execute(Game game) throws CommandExecuteException {
        game.placeShip(new Position(this.x, this.y), this.orientation);
        return true;
    }

    public Command parse(String[] commandWords) throws CommandParseException {
        AddCommand add = null;
        if (matchCommandName(commandWords[0])) {
            if (commandWords.length == 4) {
                try {
                    int x = Integer.parseInt(commandWords[1]) - 1;
                    int y = Integer.parseInt(commandWords[2]) - 1;
                    Orientation o = Orientation.parse(commandWords[3]);
                    add = new AddCommand(x, y, o);
                } catch (NumberFormatException nfe) {
                    throw new CommandParseException("[ERROR]: Invalid coordinates format for adding a ship: " + DETAILS);
                } catch (OrientationFormatException ofe) {
                    throw new CommandParseException("[ERROR]: Invalid orientation format for adding a ship: " + DETAILS);
                }
            } else
                throw new CommandParseException("[ERROR]: Incorrect number of arguments for adding a ship: " + DETAILS);
        }
        return add;
    }
}
