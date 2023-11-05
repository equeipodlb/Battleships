package view.GUI;

import controller.Controller;
import misc.Position;
import model.PlayerDTO;
import view.GameObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class StatusBar extends JPanel implements GameObserver {
    private final Controller controller;
    private JLabel currentPlayerLabel;
    private JLabel gameInfo;
    private JLabel stillAlive;
    private int currentPlayer;
    private List<PlayerDTO> players;
    private String phase = "place";

    StatusBar(Controller controller) {
        this.controller = controller;
        this.currentPlayer = controller.getCurrentPlayer();
        this.players = controller.getListOfPlayers();
        controller.addObserver(this);
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(BorderFactory.createBevelBorder(1));
        this.setPreferredSize(new Dimension(this.getWidth(), 32));
        JToolBar bar = new JToolBar();
        currentPlayerLabel = new JLabel("Current Player: " + players.get(currentPlayer).getID());
        gameInfo = new JLabel();
        gameInfo.setText("Player " + players.get(currentPlayer).getID() + " is placing his boats");
        StringBuilder str = new StringBuilder();
        str.append("Still alive: ");
        for (PlayerDTO p : players) {
            str.append(p.getID());
            str.append(" ");
        }
        stillAlive = new JLabel(str.toString());
        bar.setFloatable(false);
        bar.add(currentPlayerLabel);
        bar.add(Box.createGlue());
        bar.addSeparator();
        bar.add(gameInfo);
        bar.add(Box.createGlue());
        bar.addSeparator();
        bar.add(stillAlive);
        this.add(bar);
        this.setVisible(true);
    }

    @Override
    public void onChangedPlayer(int newCurrentPlayer, boolean real) {
        this.currentPlayer = newCurrentPlayer;
        currentPlayerLabel.setText("Current Player: " + players.get(newCurrentPlayer).getID());
        if (this.phase.equals("place"))
            gameInfo.setText("Player " + players.get(newCurrentPlayer).getID() + " is placing his boats");
    }

    @Override
    public void onPhaseChange() {
        gameInfo.setText("Attack phase has already started");
        this.phase = "attack";
    }

    @Override
    public void onSunkShip(String currentPlayerName, String attackedPlayerName) {
        gameInfo.setText("Player " + currentPlayerName + " sank a ship of player " + attackedPlayerName);
    }

    @Override
    public void onDefeatedPlayer(int player) {
        gameInfo.setText("Player " + players.get(player).getID() + " has been defeated");
        StringBuilder str = new StringBuilder();
        this.players = controller.getListOfPlayers();
        str.append("Still alive: ");
        for (PlayerDTO p : players) {
            if (!p.hasLost()) {
                str.append(p.getID());
                str.append(" ");
            }
        }
        stillAlive.setText(str.toString());
    }

    @Override
    public void onAttack(PlayerDTO player, int playerIndex, Position pos) {
        gameInfo.setText("Player " + players.get(currentPlayer).getID() + " shot player " + player.getID()
                + " in position (" + (pos.getX() + 1) + "," + (pos.getY() + 1) + ")");
    }

    @Override
    public void onSavedGame(String file) {
        gameInfo.setText("Game has been successfully saved to file " + file);
    }
}
