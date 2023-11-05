package view.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import misc.Level;

class HostConfiguration extends JPanel {
    private final int MAX_PLAYERS = 4;
    private final MainWindow main;
    private JPanel configPanel, buttonsPanel;
    private int numPlayers;
    private Level level;
    private JComboBox<Level> levelChooser;
    private JSpinner numberOfPlayersChooser;

    HostConfiguration(MainWindow main) {
        this.main = main;
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        levelChooser = new JComboBox<Level>(Level.values());
        levelChooser.setAlignmentX(Component.CENTER_ALIGNMENT);

        SpinnerNumberModel realModel = new SpinnerNumberModel(2, 2, MAX_PLAYERS, 1);
        numberOfPlayersChooser = new JSpinner(realModel);
        numberOfPlayersChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
        ((DefaultEditor) numberOfPlayersChooser.getEditor()).getTextField().setEditable(false);

        ChangeListener realListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numPlayers = (int) numberOfPlayersChooser.getValue();
            }
        };
        realModel.addChangeListener(realListener);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numPlayers = (int) numberOfPlayersChooser.getValue();
                level = (Level) levelChooser.getSelectedItem();
                main.setPlayers(0, numPlayers);
                main.setLevel(level);
                main.onNext("hosting");
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

        JPanel auxReal = new JPanel();
        auxReal.setLayout(new BoxLayout(auxReal, BoxLayout.X_AXIS));
        auxReal.add(new JLabel("Number of players:"));
        auxReal.add(numberOfPlayersChooser);
        configPanel.add(auxReal);
        configPanel.add(Box.createVerticalGlue());

        JPanel pnAux = new JPanel(new FlowLayout());
        JPanel pn = new JPanel();
        pn.setLayout(new BoxLayout(pn, BoxLayout.X_AXIS));
        pn.add(Box.createGlue());
        pn.add(Box.createGlue());
        pnAux.add(pn);
        configPanel.add(pnAux);
        configPanel.add(Box.createVerticalGlue());

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
