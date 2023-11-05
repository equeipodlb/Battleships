package view.GUI;

import controller.ClientController;
import controller.Controller;
import controller.ControllerGUI;
import controller.ServerController;
import exceptions.CommandExecuteException;
import exceptions.GameParseException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.PlayerDTO;
import view.GameObserver;
import web.Servidor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Stack;

public class MainWindow extends JFrame implements GameObserver {
    private static MainWindow uniqueMainWindow = null;
    private Controller controller;
    private boolean single, local, loaded = false;
    private Level level;
    private int currentPlayer, currentShip, realPlayers, botPlayers, onlinePlayerIndex;
    private Orientation orientation;
    private List<String> playerNames;
    private Stack<JPanel> previous;
    private StartScreen startScreen;
    private ModeWindow modeWindow;
    private LocalWindow localWindow;
    private PlayersNames playersNames;
    private HelpWindow helpWindow;
    private ConfigurationLocal configurationLocal;
    private JPanel mainPanel;
    private ShipsBar shipsBar;
    private List<Board> boards;
    private StatusBar statusBar;
    private List<PlayerDTO> players;
    private ConfigurationOnline configurationOnline;
    private HostConfiguration hostConfig;
    private JoinConfiguration joinConfig;
    private HostingWindow hostingWindow;
    private String connectingAddress;
    private String phase = "placing";
    private JMenuBar menuBar;
    private JFileChooser fc;
    private ServerController servctrl;
    private ConnectingWindow connectingWindow;
    private EndWindow endWindow;

    private MainWindow(ControllerGUI ctrl) {
        super("Battleship");
        ctrl.addObserver(this);
        controller = ctrl;
        initGUI();
    }

    public static MainWindow getInstance(ControllerGUI controller) {
        if (uniqueMainWindow == null) uniqueMainWindow = new MainWindow(controller);
        return uniqueMainWindow;
    }

    private void initGUI() {
        phase = "placing";
        currentPlayer = 0;
        players = controller.getListOfPlayers();
        this.onlinePlayerIndex = 0;
        botPlayers = 0;
        realPlayers = 0;
        level = null;
        single = true;
        local = true;
        loaded = false;
        boards = new ArrayList<>();
        playerNames = new ArrayList<>();
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        previous = new Stack<>();
        startScreen = new StartScreen(this);
        mainPanel.add(startScreen);
        this.add(Box.createGlue());
        this.add(mainPanel);
        this.add(Box.createGlue());
        helpWindow = new HelpWindow(this);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * This method is in charge of navigating the user through the initial windows and configurations prior to starting a game.
     *
     * @param next a {@code String} that represents the next window the player will see.
     */
    void onNext(String next) {
        switch (next) {
            case "start":
                controller.reset();
                this.getContentPane().removeAll();
                initGUI();
                this.revalidate();
                this.repaint();
                break;
            case "play":
                modeWindow = new ModeWindow(this);
                previous.add(startScreen);
                mainPanel.remove(startScreen);
                mainPanel.add(modeWindow);
                this.revalidate();
                this.repaint();
                break;
            case "help":
                previous.add((JPanel) mainPanel.getComponent(0));
                mainPanel.remove(mainPanel.getComponent(0));
                mainPanel.add(helpWindow);
                this.revalidate();
                this.repaint();
                break;
            case "single":
                configurationLocal = new ConfigurationLocal(this);
                previous.add(modeWindow);
                mainPanel.remove(modeWindow);
                mainPanel.add(configurationLocal);
                this.revalidate();
                this.repaint();
                break;
            case "multi":
                localWindow = new LocalWindow(this);
                previous.add(modeWindow);
                mainPanel.remove(modeWindow);
                mainPanel.add(localWindow);
                this.revalidate();
                this.repaint();
                break;
            case "local":
                configurationLocal = new ConfigurationLocal(this);
                previous.add(localWindow);
                mainPanel.remove(localWindow);
                mainPanel.add(configurationLocal);
                this.revalidate();
                this.repaint();
                break;
            case "online":
                configurationOnline = new ConfigurationOnline(this);
                previous.add(localWindow);
                mainPanel.remove(localWindow);
                mainPanel.add(configurationOnline);
                local = false;
                this.revalidate();
                this.repaint();
                break;
            case "starthost":
                hostConfig = new HostConfiguration(this);
                previous.add(configurationOnline);
                mainPanel.remove(configurationOnline);
                mainPanel.add(hostConfig);
                this.revalidate();
                this.repaint();
                break;
            case "join":
                joinConfig = new JoinConfiguration(this);
                previous.add(configurationOnline);
                mainPanel.remove(configurationOnline);
                mainPanel.add(joinConfig);
                this.revalidate();
                this.repaint();
                break;
            case "hosting":
                hostingWindow = new HostingWindow(realPlayers, this);
                previous.add(hostConfig);
                mainPanel.remove(hostConfig);
                mainPanel.add(hostingWindow);
                this.revalidate();
                this.repaint();
                Game game = Game.getInstance();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            Servidor server = new Servidor(realPlayers); // Se crea el servior para aceptar a los otros jugadores
                            servctrl = new ServerController(realPlayers, level, game, server, hostingWindow); // Para poder conectar al propio host a su servidor
                        } catch (IOException e) {
                        }
                    }
                });

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        servctrl.retrieveNames(playerNames);
                        onNext("startGameHost");
                    }
                });
                break;
            case "startGameHost":
                try {
                    servctrl.setConfig(level, realPlayers, botPlayers, playerNames);
                    servctrl.sendConfig(level, realPlayers, playerNames);
                } catch (CommandExecuteException e1) {
                    e1.printStackTrace();
                }
                shipsBar = new ShipsBar(this, servctrl);
                players = servctrl.getListOfPlayers();
                previous.add((JPanel) mainPanel.getComponent(0));
                mainPanel.remove(mainPanel.getComponent(0));
                this.revalidate();
                this.repaint();
                servctrl.placingPhase();
                servctrl.attackingPhase();
                break;
            case "connecting":
                try {
                    controller = new ClientController(connectingAddress);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Unknown Host", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                connectingWindow = new ConnectingWindow();
                previous.add(joinConfig);
                mainPanel.remove(joinConfig);
                mainPanel.add(connectingWindow);
                this.revalidate();
                this.repaint();
                controller.addObserver(this);
                if (controller.getStart()) {
                    controller.sendName(joinConfig.getPlayerName());
                    controller.getNotify(); // Recibe la configuracion
                    this.players = controller.getListOfPlayers();
                    this.level = controller.getLevel();
                    this.onlinePlayerIndex = controller.getIndex();
                    onNext("board");
                }
                break;
            case "playerNames":
                playersNames = new PlayersNames(this, realPlayers);
                previous.add(configurationLocal);
                mainPanel.remove(configurationLocal);
                mainPanel.add(playersNames);
                this.revalidate();
                this.repaint();
                break;
            case "board":
                if (local) {
                    try {
                        controller.setConfig(level, realPlayers, botPlayers, playerNames);
                        players = controller.getListOfPlayers();
                    } catch (CommandExecuteException e) {
                        e.printStackTrace();
                    }
                }
                if (!loaded) {
                    shipsBar = new ShipsBar(this, controller);
                }
                previous.add((JPanel) mainPanel.getComponent(0));
                mainPanel.remove(mainPanel.getComponent(0));
                addBoards();
                this.revalidate();
                this.repaint();
                break;
            case "load":
                load();
                if (loaded) {
                    currentPlayer = controller.getCurrentPlayer();
                    level = controller.getLevel();
                    onNext("board");
                    controller.changePhase();
                }
                break;
            case "end":
                mainPanel.removeAll();
                mainPanel.setLayout(new FlowLayout());
                mainPanel.add(endWindow);
                this.revalidate();
                this.repaint();
                break;
            case "exit":
                this.dispose();
                break;
        }
    }

    void onCancel() {
        if (previous.empty()) dispose();
        else {
            JPanel prev = previous.pop();
            mainPanel.removeAll();
            mainPanel.add(prev);
            this.revalidate();
            this.repaint();
        }
    }

    private void addBoards() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double height = screenSize.getHeight();
        int buttonDim;
        mainPanel.setLayout(new BorderLayout());
        if (!loaded) mainPanel.add(shipsBar, BorderLayout.PAGE_START);
        JPanel aux1 = new JPanel();
        JPanel aux2 = new JPanel();
        aux2.setLayout(new BoxLayout(aux2, BoxLayout.Y_AXIS));
        switch (players.size()) {
            case 2:
                buttonDim = (int) ((height - 230) / level.getDimY());
                JPanel jp = new JPanel();
                jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
                jp.add(Box.createGlue());
                for (int i = 0; i < 2; ++i) {
                    Board board = new Board(this, controller, players.get(i), i, buttonDim, buttonDim);
                    boards.add(board);
                    jp.add(board);
                }
                jp.add(Box.createGlue());
                aux2.add(jp);
                break;
            case 3:
            case 4:
                buttonDim = (int) ((height - 280) / (2 * level.getDimY()));
                JPanel top = new JPanel(), bottom = new JPanel();
                top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
                bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
                for (int i = 0; i < 2; ++i) {
                    Board board = new Board(this, controller, players.get(i), i, buttonDim, buttonDim);
                    boards.add(board);
                    top.add(board);
                }
                bottom.add(Box.createGlue());
                for (int i = 2; i < players.size(); ++i) {
                    Board board = new Board(this, controller, players.get(i), i, buttonDim, buttonDim);
                    boards.add(board);
                    bottom.add(board);
                }
                bottom.add(Box.createGlue());
                aux2.add(top);
                aux2.add(bottom);
                break;
        }
        aux1.add(aux2);
        mainPanel.add(aux1, BorderLayout.CENTER);

        statusBar = new StatusBar(controller);
        mainPanel.add(statusBar, BorderLayout.PAGE_END);
    }

    private void addMenuBar() {
        menuBar = new JMenuBar();
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        menuBar.add(save);
        this.setJMenuBar(menuBar);
    }

    int getOnlinePlayerIndex() {
        return onlinePlayerIndex;
    }

    private void save() {
        fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Game save (.json)", "json");
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(filter);
        int returnValue = fc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            if (selectedFile == null) return;
            if (selectedFile.toString().endsWith(".json"))
                selectedFile = new File(selectedFile.toString().substring(0, selectedFile.toString().length() - 5));
            controller.save(selectedFile.toString().trim());
        }
    }

    private void load() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Game save (.json)", "json");
        fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(filter);
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            if (selectedFile == null) return;
            try {
                controller.load(selectedFile.toString());
                loaded = true;
            } catch (FileNotFoundException | GameParseException e) {
                JOptionPane.showMessageDialog(null, "An error occured when loading the game");
            }
        }
    }

    boolean isSingle() {
        return single;
    }

    void setSingle(boolean b) {
        this.single = b;
    }

    boolean isLocal() {
        return local;
    }

    void setLocal(boolean b) {
        this.local = b;
    }

    void setPlayers(int botPlayers, int realPlayers) {
        this.botPlayers = botPlayers;
        this.realPlayers = realPlayers;
    }

    void setLevel(Level level) {
        this.level = level;
    }

    void setPlayerNames(List<String> names) {
        this.playerNames = names;
    }

    void placeShip(int x, int y) throws CommandExecuteException {
        controller.placeShip(currentShip, new Position(x, y), orientation);
        currentShip = -1;
    }

    void setSelectedShip(int i, Orientation o) {
        currentShip = i;
        orientation = o;
        controller.setCurrentShip(i);
    }

    int getCurrentPlayer() {
        return currentPlayer;
    }

    void attackPos(int x, int y, int player) throws CommandExecuteException {
        controller.attackPos(currentPlayer, new Position(x, y), player);
    }

    int getCurrentShip() {
        return currentShip;
    }

    String getPhase() {
        return phase;
    }

    void setConnectingAddress(String address) {
        this.connectingAddress = address;
    }

    @Override
    public void onChangedPlayer(int currentPlayerIndex, boolean real) {
        this.currentPlayer = currentPlayerIndex;
    }

    @Override
    public void onPhaseChange() {
        addMenuBar();
        phase = "attacking";
        if (shipsBar != null)
            this.remove(shipsBar);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void endScreen(PlayerDTO winner) {
        this.setJMenuBar(null);
        this.endWindow = new EndWindow(this, winner.getID());
        onNext("end");
    }
}
