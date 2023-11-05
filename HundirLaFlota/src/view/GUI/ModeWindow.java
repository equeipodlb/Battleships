package view.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ModeWindow extends JPanel {
    private final MainWindow main;
    private JButton singleButton, multiButton, cancelButton;

    ModeWindow(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        singleButton = new JButton("Singleplayer");
        singleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setSingle(true);
                main.onNext("single");
            }
        });
        singleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        multiButton = new JButton("Multiplayer");
        multiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setSingle(false);
                main.onNext("multi");
            }
        });
        multiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onCancel();
            }
        });
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalGlue());
        this.add(singleButton);
        this.add(Box.createVerticalGlue());
        this.add(multiButton);
        this.add(Box.createVerticalGlue());
        this.add(cancelButton);
        this.add(Box.createVerticalGlue());
    }
}
