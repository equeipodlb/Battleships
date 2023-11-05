package view.GUI;

import controller.Controller;
import misc.Level;
import misc.Orientation;
import model.PlayerDTO;
import view.GameObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import static utils.IconUtils.resizedIcon;

class ShipsBar extends JPanel implements GameObserver {
    private final String ICON_PATH = "resources/icons/";
    private final MainWindow main;
    private final Level level;
    private List<JButton> buttons;

    ShipsBar(MainWindow main, Controller ctrl) {
        this.main = main;
        this.level = ctrl.getLevel();
        ctrl.addObserver(this);
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(BorderFactory.createBevelBorder(1));
        buttons = new ArrayList<>();
        this.add(Box.createGlue());
        for (int i = 0; i < level.getNumberOfShips(); ++i) {
            int length = level.getShipLength(i);
            String path = ICON_PATH + length + "/" + length + ".png";
            JButton aux = new JButton();
            aux.setPreferredSize(new Dimension(75 * length, 50));
            aux.setBorderPainted(false);
            aux.setOpaque(false);
            aux.setIcon(resizedIcon(new ImageIcon(path), aux));
            buttons.add(aux);
            this.add(aux);
            this.add(Box.createGlue());

            int finalI = i;
            aux.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Orientation o;
                    if (e.getButton() == MouseEvent.BUTTON1)
                        o = Orientation.HORIZONTAL;
                    else if (e.getButton() == MouseEvent.BUTTON3) o = Orientation.VERTICAL;
                    else return;
                    main.setSelectedShip(finalI, o);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }
    }

    private void reset() {
        this.removeAll();
        initGUI();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onPlacedShip(int currentPlayer, PlayerDTO player) { //elimina el boton de esta barra;
        for (int i = 0; i < this.getComponents().length; i++) {
            if (buttons.get(main.getCurrentShip()) == this.getComponents()[i]) {
                this.remove(i - 1); // elimina el Glue a la izquierda del botón
                break;
            }
        }
        this.remove(buttons.get(main.getCurrentShip())); // elimina el botón
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onChangedPlayer(int player, boolean real) {
        if (main.getPhase().equals("placing")) {
            if (real && main.isLocal())
                this.reset();
        }
    }

    @Override
    public void onPhaseChange() {
        this.setVisible(false);
    }
}
