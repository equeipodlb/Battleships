package view.GUI;

import controller.Controller;
import exceptions.CommandExecuteException;
import misc.Cell;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.PlayerDTO;
import view.GameObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static utils.IconUtils.resizedIcon;

class Board extends JPanel implements GameObserver {
    private final String ICON_PATH = "resources/icons/";
    private final int btnHeight;
    private final int btnWidth;
    private final MainWindow main;
    private final Controller controller;
    private final List<JButton> buttonList;
    private final int rows;
    private final int cols;
    private final int playerIndex;
    private final int onlinePlayerIndex;
    private PlayerDTO player;

    Board(MainWindow main, Controller controller, PlayerDTO player, int playerIndex, int btnWidth, int btnHeight) {
        this.main = main;
        this.controller = controller;
        this.playerIndex = playerIndex;
        this.onlinePlayerIndex = main.getOnlinePlayerIndex();
        Level level = controller.getLevel();
        buttonList = new ArrayList<>();
        this.cols = level.getDimX();
        this.rows = level.getDimY();
        this.btnWidth = btnWidth;
        this.btnHeight = btnHeight;
        controller.addObserver(this);
        this.player = player;
        initGUI();
    }

    private void initGUI() {
        Dimension dim = new Dimension(btnWidth * cols + 75, btnHeight * rows + 60);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1, false), player.getID()));
        this.setLayout(new FlowLayout());
        draw(player); // Despues cambiar el newPlayer por player
        setListeners("placing");
        this.setVisible(true);
    }

    ActionListener placingListener(int row, int col) {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((main.isLocal() && main.getCurrentPlayer() == playerIndex) || (!main.isLocal() && onlinePlayerIndex == main.getCurrentPlayer() && main.getCurrentPlayer() == playerIndex)) {
                    try {
                        main.placeShip(col, row);
                    } catch (CommandExecuteException cee) {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(Board.this), cee.getMessage());
                    }
                }
            }
        };
        return listener;
    }

    private ActionListener attackingListener(int row, int col) {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (main.isLocal() || (!main.isLocal() && main.getCurrentPlayer() == onlinePlayerIndex)) {
                    try {
                        main.attackPos(col, row, playerIndex);
                    } catch (CommandExecuteException cee) {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(Board.this), cee.getMessage());
                    }
                }
            }

        };
        return listener;
    }

    private void setListeners(String phase) {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                JButton button = buttonList.get(i * cols + j);
                for (ActionListener al : button.getActionListeners()) button.removeActionListener(al);
                ActionListener al = phase.equals("placing") ? placingListener(i, j) : attackingListener(i, j);
                button.addActionListener(al);
            }
        }
    }

    private void draw(PlayerDTO player) {
        this.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton aux = new JButton();
                aux.setPreferredSize(new Dimension(btnWidth, btnHeight));
                Cell cell = player.getCell(j, i);
                drawButton(aux, cell);
                buttonList.add(aux);
                this.add(aux);
            }
        }
        this.revalidate();
        this.repaint();
    }

    private void redrawButtons(PlayerDTO player) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton aux = buttonList.get(i * cols + j);
                Cell cell = player.getCell(j, i);
                drawButton(aux, cell);
            }
        }
    }

    private void drawButton(JButton button, Cell cell) {
        switch (cell) {
            case HIT:
                if ((main.isLocal() && (!cell.isSunk() && main.getCurrentPlayer() != playerIndex)) || (!main.isLocal() && playerIndex != onlinePlayerIndex && !cell.isSunk())) {
                    button.setIcon(resizedIcon(new ImageIcon(ICON_PATH + "hit.png"), button));
                    break; // si no se cumplen las condiciones pasa al caso SHIP. No mover este break.
                }
            case SHIP:
                if ((main.isLocal() && (main.getCurrentPlayer() == playerIndex || cell.isSunk())) || (!main.isLocal() && (onlinePlayerIndex == playerIndex || cell.isSunk()))) {
                    StringBuilder str = new StringBuilder(cell.getLength() + "/" + cell.getLength());
                    str.append(cell.getShipPos() + 1);
                    if (cell.getOrientation() == Orientation.HORIZONTAL) str.append("H");
                    else if (cell.getOrientation() == Orientation.VERTICAL) str.append("V");
                    if (cell.isHit()) str.append("H");
                    str.append(".png");
                    button.setIcon(resizedIcon(new ImageIcon(ICON_PATH + str), button));
                } else
                    button.setIcon(resizedIcon(new ImageIcon(ICON_PATH + "empty.png"), button));
                break;
            case MISS:
                button.setIcon(resizedIcon(new ImageIcon(ICON_PATH + "boya.png"), button));
                break;
            case EMPTY:
                button.setIcon(resizedIcon(new ImageIcon(ICON_PATH + "empty.png"), button));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + cell);
        }
    }

    @Override
    public void onPhaseChange() {
        redrawButtons(this.player);
        setListeners("attacking");
    }

    @Override
    public void onAttack(PlayerDTO player, int playerIndex, Position pos) {
        if ((!main.isLocal() && playerIndex == onlinePlayerIndex && playerIndex == this.playerIndex) || (main.isLocal() && playerIndex == this.playerIndex)) {
            this.player = player;
            redrawButtons(player);
        }
    }

    @Override
    public void onChangedPlayer(int player, boolean real) {
        if (main.isLocal()) {
            redrawButtons(this.player);
        }
    }

    @Override
    public void onPlacedShip(int currentPlayer, PlayerDTO player) {
        if ((!main.isLocal() && currentPlayer == onlinePlayerIndex && currentPlayer == this.playerIndex) || (main.isLocal() && currentPlayer == this.playerIndex)) {
            this.player = player;
            redrawButtons(player);
        }
    }
}
