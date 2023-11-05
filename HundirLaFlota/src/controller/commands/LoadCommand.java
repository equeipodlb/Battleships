package controller.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import exceptions.GameParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import model.Game;

public class LoadCommand extends Command {
    private static final String NAME = "load";
    private static final String SHORTCUT = "l";
    private static final String HELP = "loads a game from a file";
    private static final String DETAILS = "[l]oad <directory>";

    private String filename;

    public LoadCommand() {
        super(NAME, SHORTCUT, DETAILS, HELP);
    }

    @Override
    public boolean execute(Game game) throws CommandExecuteException {
        try {
            InputStream is = new FileInputStream(new File(filename + ".json"));
            JSONObject jsonInput = new JSONObject(new JSONTokener(is));
            try {
                game.load(jsonInput);
            } catch (GameParseException gpe) {
                throw new CommandExecuteException();
            }
            return true;
        } catch (FileNotFoundException fnfe) {
            throw new CommandExecuteException(fnfe.getMessage());
        }
    }

    @Override
    public Command parse(String[] commandWords) throws CommandParseException {
        if (commandWords[0].equalsIgnoreCase("l") || commandWords[0].equalsIgnoreCase("load")) {
            if (commandWords.length == 2) {
                this.filename = commandWords[1];
                return this;
            } else throw new CommandParseException("[ERROR]: Invalid argument for load command:" + super.helpText());
        }
        return null;
    }
}
