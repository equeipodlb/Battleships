package controller.commands;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import misc.Position;
import model.Game;

class AttackCommand extends Command {
    private static final String NAME = "attack";
    private static final String SHORTCUT = "t";
    private static final String HELP = "attack a player's ship in position x, y";
    private static final String DETAILS = "a[t]tack <p> <x> <y>";
    private int atackedPlayer, x, y;
    private String s;

    public AttackCommand() {
        super(NAME, SHORTCUT, DETAILS, HELP);
    }

    public AttackCommand(int atackedPlayer, String s, int num1, int num2) {
        super(NAME, SHORTCUT, DETAILS, HELP);
        this.atackedPlayer = atackedPlayer;
        this.s = s;
        this.x = num1;
        this.y = num2;
    }

    public boolean execute(Game game) throws CommandExecuteException {
        if (atackedPlayer > -1) game.attackShip(atackedPlayer, new Position(this.x, this.y));
        else game.attackShip(s, new Position(this.x, this.y));
        return true;
    }

    public Command parse(String[] commandWords) throws CommandParseException {
        AttackCommand attack = null;
        if (matchCommandName(commandWords[0])) {
            if (commandWords.length == 4) {
                try {
                    String s = null;
                    int p = -1;
                    try {
                        p = Integer.parseInt(commandWords[1]);
                        if (p < 1) throw new CommandParseException("[ERROR]: Incorrect player number");
                        p--;
                    } catch (NumberFormatException nfe) {
                        s = commandWords[1];
                    }
                    int x = Integer.parseInt(commandWords[2]) - 1;
                    int y = Integer.parseInt(commandWords[3]) - 1;
                    attack = new AttackCommand(p, s, x, y);
                } catch (NumberFormatException nfe) {
                    throw new CommandParseException("[ERROR]: Invalid data format for attacking a player's ship: " + DETAILS);
                }
            } else
                throw new CommandParseException("[ERROR]: Incorrect number of arguments for attacking a player's ship: " + DETAILS);
        }
        return attack;
    }
}
