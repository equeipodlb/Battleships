package view.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LocalWindow extends JPanel {
    private final MainWindow main;
    private JButton localButton, onlineButton, cancelButton;

    LocalWindow(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        localButton = new JButton("Local");
        localButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setLocal(true);
                main.onNext("local");
            }
        });
        localButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        onlineButton = new JButton("Online");
        onlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setLocal(false);
                main.onNext("online");
            }
        });
        onlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onCancel();
            }
        });
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalGlue());
        this.add(localButton);
        this.add(Box.createVerticalGlue());
        this.add(onlineButton);
        this.add(Box.createVerticalGlue());
        this.add(cancelButton);
        this.add(Box.createVerticalGlue());
    }
}
