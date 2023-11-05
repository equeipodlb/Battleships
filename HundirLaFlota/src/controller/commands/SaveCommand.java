package controller.commands;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import model.Game;

class SaveCommand extends Command {
    private static final String NAME = "save";
    private static final String SHORTCUT = "s";
    private static final String HELP = "saves the game";
    private static final String DETAILS = "[s]ave <directory>";

    private String filename;

    public SaveCommand() {
        super(NAME, SHORTCUT, DETAILS, HELP);
    }

    @Override
    public boolean execute(Game game) throws CommandExecuteException {
        game.save(filename);
        return false;
    }

    @Override
    public Command parse(String[] commandWords) throws CommandParseException {
        if (commandWords[0].equalsIgnoreCase("s") || commandWords[0].equalsIgnoreCase("save")) {
            if (commandWords.length == 2) {
                this.filename = commandWords[1];
                return this;
            } else throw new CommandParseException("[ERROR]: Invalid argument for save command:" + super.helpText());
        }
        return null;
    }
}
