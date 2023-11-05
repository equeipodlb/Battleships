package view.GUI;

import javax.swing.JLabel;
import javax.swing.JPanel;

class ConnectingWindow extends JPanel {
    private final JLabel info;
    private boolean start;

    ConnectingWindow() {
        this.info = new JLabel("Waiting for the game to start... ");
        start = false;
        initGUI();
    }

    private void initGUI() {
        info.setSize(500, 500);
        info.setVisible(true);
        this.add(info);
    }

    public boolean getStart() {
        return start;
    }

    public void setStart(boolean b) {
        this.start = b;
    }
}
