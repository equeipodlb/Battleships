package main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.*;

import controller.Controller;
import controller.ControllerConsole;
import controller.ControllerGUI;
import model.Game;
import view.GUI.MainWindow;
import view.console.GamePrinter;

public class Main {
    private final static String _modeDefaultValue = "gui";
    private static Game game;
    private static Controller controller = null;
    private static GamePrinter printer = null;
    private static MainWindow mainWindow = null;
    private static String _mode = null;

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        try {
            init();
            start(args);
        } catch (Exception e) {
            System.err.println("Something went wrong ...");
            System.err.println();
            e.printStackTrace();
        }
    }

    private static void init() {
        game = Game.getInstance();
    }

    private static void start(String[] args) throws Exception {
        parseArgs(args);
        if (_mode.equals("console"))
            startConsoleMode();
        else
            startGUIMode();
    }

    private static void startConsoleMode() throws Exception {
        controller = new ControllerConsole(game);
        printer = new GamePrinter((ControllerConsole) controller);
        controller.run();
    }

    private static void startGUIMode() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                controller = new ControllerGUI(game);
                mainWindow = MainWindow.getInstance((ControllerGUI) controller);
            }
        });
    }

    private static void parseArgs(String[] args) {
        Options cmdLineOptions = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelp(line, cmdLineOptions);
            parseMode(line);
            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                String error = "Illegal arguments:";
                for (String o : remaining)
                    error += (" " + o);
                throw new ParseException(error);
            }
        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static void parseHelp(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
            System.exit(0);
        }
    }

    private static void parseMode(CommandLine line) throws ParseException {
        String m = line.getOptionValue("m", _modeDefaultValue);
        if (!m.equals("console") && !m.equals("gui")) {
            throw new ParseException("Invalid mode: " + m);
        } else
            _mode = m;
    }

    private static Options buildOptions() {
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());
        cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
                .desc("Execution Mode. Possible values: 'console'\n" + "(Console mode), 'gui' (Graphical User\n"
                        + "Interface mode). Default value: " + _modeDefaultValue + ".")
                .build());
        return cmdLineOptions;
    }
}