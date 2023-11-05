package view.GUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

class ConfigurationOnline extends JPanel {
    private final MainWindow main;
    private JButton hostButton, joinButton, cancelButton;

    ConfigurationOnline(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        hostButton = new JButton("Host");
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setSingle(true);
                main.onNext("starthost");
            }
        });
        hostButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton = new JButton("Join game");
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setSingle(false);
                main.onNext("join");
            }
        });
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onCancel();
            }
        });
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalGlue());
        this.add(hostButton);
        this.add(Box.createVerticalGlue());
        this.add(joinButton);
        this.add(Box.createVerticalGlue());
        this.add(cancelButton);
        this.add(Box.createVerticalGlue());
    }
}
