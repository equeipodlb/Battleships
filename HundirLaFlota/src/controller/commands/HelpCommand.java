package controller.commands;

import exceptions.CommandParseException;
import model.Game;

class HelpCommand extends Command {
    public static final String HELP = "show this help";
    public static final String DETAILS = "[h]elp";
    private static final String NAME = "help";
    private static final String SHORTCUT = "h";

    public HelpCommand() {
        super(NAME, SHORTCUT, DETAILS, HELP);
    }

    public boolean execute(Game game) {
        String aux = "Available commands:\n" + CommandGenerator.commandHelp();
        System.out.println(aux);
        return false;
    }

    public Command parse(String[] commandWords) throws CommandParseException {
        return this.parseNoParamsCommand(commandWords);
    }
}
