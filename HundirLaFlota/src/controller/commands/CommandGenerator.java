package controller.commands;

import exceptions.CommandParseException;

public class CommandGenerator {
    public static final String unknownCommandMsg = "Unknown command";
    private static final Command[] availableCommands = {new AddCommand(), new AttackCommand(), new HelpCommand(), new SaveCommand(), new LoadCommand()};
    private static final Command[] availablePlacingCommands = {new AddCommand(), new HelpCommand(), new SaveCommand()};
    private static final Command[] availableAttackingCommands = {new AttackCommand(), new HelpCommand(), new SaveCommand()};
    private static Command[] currentCommands;

    public CommandGenerator() {
        super();
    }

    public static String commandHelp() {
        String devuelve = "";
        for (int i = 0; i < currentCommands.length; ++i)
            devuelve += currentCommands[i].helpText();
        return devuelve;
    }

    public static void setPlacingCommands() {
        currentCommands = availablePlacingCommands;
    }

    public static void setAttackingCommands() {
        currentCommands = availableAttackingCommands;
    }

    public static Command parse(String[] commandWords) throws CommandParseException {
        Command c = null;
        for (int i = 0; c == null && i < currentCommands.length; ++i)
            c = currentCommands[i].parse(commandWords);
        if (c == null) throw new CommandParseException("[ERROR]: " + unknownCommandMsg);
        else return c;
    }
}

