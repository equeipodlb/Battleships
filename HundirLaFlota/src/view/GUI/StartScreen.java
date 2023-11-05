package view.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StartScreen extends JPanel {
    private final MainWindow main;
    private JButton playButton;
    private JButton loadButton;
    private JButton exitButton;
    private JButton helpButton;

    StartScreen(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onNext("play");
            }
        });
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onNext("load");
            }
        });
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onNext("help");
            }
        });
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onCancel();
            }
        });
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(Box.createVerticalGlue());
        this.add(playButton);
        this.add(Box.createVerticalGlue());
        this.add(loadButton);
        //this.add(Box.createVerticalGlue());
        //this.add(helpButton);
        this.add(Box.createVerticalGlue());
        this.add(exitButton);
        this.add(Box.createVerticalGlue());
    }
}
