package view.GUI;

import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import misc.Level;

class ConfigurationLocal extends JPanel {
    private final int MAX_PLAYERS = 4;
    private final MainWindow main;
    private JPanel configPanel, buttonsPanel;
    private int realPlayers, botPlayers;
    private Level level;
    private JComboBox levelChooser;
    private JSpinner botPlayersChooser, realPlayersChooser;

    ConfigurationLocal(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        levelChooser = new JComboBox<Level>(Level.values());
        levelChooser.setAlignmentX(Component.CENTER_ALIGNMENT);

        int minBots = main.isSingle() ? 1 : 0;

        SpinnerNumberModel botModel = new SpinnerNumberModel(minBots, minBots, 3, 1);
        botPlayersChooser = new JSpinner(botModel);
        botPlayersChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
        ((DefaultEditor) botPlayersChooser.getEditor()).getTextField().setEditable(false);

        SpinnerNumberModel realModel = new SpinnerNumberModel(2, 2, 4, 1);
        realPlayersChooser = new JSpinner(realModel);
        realPlayersChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
        ((DefaultEditor) realPlayersChooser.getEditor()).getTextField().setEditable(false);

        ChangeListener realListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ChangeListener aux = botModel.getListeners(ChangeListener.class)[0];
                botModel.removeChangeListener(aux);
                botModel.setMaximum(MAX_PLAYERS - (int) realPlayersChooser.getValue());
                botModel.addChangeListener(aux);
            }
        };
        ChangeListener botListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                realModel.removeChangeListener(realListener);
                realModel.setMaximum(MAX_PLAYERS - (int) botPlayersChooser.getValue());
                realModel.addChangeListener(realListener);
            }
        };
        realModel.addChangeListener(realListener);
        botModel.addChangeListener(botListener);
        if (!main.isSingle()) realListener.stateChanged(new ChangeEvent(this));
        botListener.stateChanged(new ChangeEvent(this));

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botPlayers = (int) botPlayersChooser.getValue();
                realPlayers = 1;
                if (!main.isSingle()) realPlayers = (int) realPlayersChooser.getValue();
                level = (Level) levelChooser.getSelectedItem();
                main.setPlayers(botPlayers, realPlayers);
                main.setLevel(level);
                main.onNext("playerNames");
            }
        });

        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.onCancel();
            }
        });

        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(confirmButton);

        JPanel aux = new JPanel();
        aux.setLayout(new BoxLayout(aux, BoxLayout.X_AXIS));
        configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));

        configPanel.add(Box.createVerticalGlue());
        configPanel.add(levelChooser);

        configPanel.add(Box.createVerticalGlue());
        JPanel auxBots = new JPanel();
        auxBots.setLayout(new BoxLayout(auxBots, BoxLayout.X_AXIS));
        auxBots.add(new JLabel("Number of bots:"));
        auxBots.add(botPlayersChooser);
        configPanel.add(auxBots);
        configPanel.add(Box.createVerticalGlue());

        if (!main.isSingle()) {
            JPanel auxReal = new JPanel();
            auxReal.setLayout(new BoxLayout(auxReal, BoxLayout.X_AXIS));
            auxReal.add(new JLabel("Number of real players:"));
            auxReal.add(realPlayersChooser);
            configPanel.add(auxReal);
            configPanel.add(Box.createVerticalGlue());
        }

        aux.add(Box.createGlue());
        aux.add(configPanel);
        aux.add(Box.createGlue());

        this.add(Box.createVerticalGlue());
        this.add(aux);
        this.add(Box.createVerticalGlue());
        this.add(buttonsPanel);
        this.add(Box.createVerticalGlue());
    }
}


