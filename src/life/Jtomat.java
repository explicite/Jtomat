package life;

import life.cell.Dimension;
import life.model.JtomatModel;
import life.rule.Rule;
import life.view.CellsPanel;
import life.view.FigurePanel;
import life.view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Author: Jan Paw
 * Date: 4/12/12
 * Time: 11:20 AM
 */
public class Jtomat extends JFrame {
    private CellsPanel cellsPanel;
    private JTextField ruleField;
    private JButton start;
    private JButton reset;
    private GraphPanel graphPanel;
    private JPanel areaPanel;
    private JPanel graph;
    private JPanel control;
    private JCheckBox useCustomFigureCheckBox;
    private JComboBox figureBox;
    private JPanel figurePanel;
    private FigurePanel figureCanvas;
    private JButton screenButton;
    private JtomatModel jtomatModel;
    private Dimension dimension;
    private String[] data = {"23/3", "1357/1357", "45678/3", "2345/45678", "245/368", "34678/3678"};

    private void createUIComponents() throws InterruptedException {
        dimension = new Dimension(300, 300);
        jtomatModel = new JtomatModel(dimension, 100);
        cellsPanel = new CellsPanel(dimension, 2, jtomatModel);
        figureBox = new JComboBox(data);
        figureCanvas = new FigurePanel();
    }

    public Jtomat(String title) throws HeadlessException {
        super(title);

        screenButton.setEnabled(false);

        screenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellsPanel.takeScreen();
            }
        });

        figureBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ruleField.setText((String) ((JComboBox) e.getSource()).getSelectedItem());
            }
        });

        graphPanel.setJtomatModel(jtomatModel);
        jtomatModel.setStartFigure(figureCanvas.getGridArray());
        start.setActionCommand("start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("start"))
                    start();
                else if (e.getActionCommand().equals("pause"))
                    pause();
                else if (e.getActionCommand().equals("resume"))
                    resume();
            }
        });

        reset.setEnabled(true);
        reset.setText("generate");
        reset.setActionCommand("generate");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("reset"))
                    reset();
                else if (e.getActionCommand().equals("generate")) {
                    generate();
                }
            }
        });
        setContentPane(areaPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void start() {
        jtomatModel.setRule(new Rule(ruleField.getText()));
        if (useCustomFigureCheckBox.isSelected())
            jtomatModel.setStartFigure(figureCanvas.getGridArray());
        else
            jtomatModel.breathOfLife();
        jtomatModel.start();
        start.setText("pause");
        start.setActionCommand("pause");
        reset.setEnabled(false);
        useCustomFigureCheckBox.setEnabled(false);
    }

    private void generate() {
        reset.setText("reset");
        reset.setActionCommand("reset");
        if (useCustomFigureCheckBox.isSelected())
            jtomatModel.setStartFigure(figureCanvas.getGridArray());
        else
            jtomatModel.breathOfLife();
        graphPanel.reset();
    }

    private void pause() {
        try {
            jtomatModel.stop();
            reset.setEnabled(true);
            start.setText("resume");
            start.setActionCommand("resume");
            useCustomFigureCheckBox.setEnabled(true);
            screenButton.setEnabled(true);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            pause();
        }
    }

    private void resume() {
        screenButton.setEnabled(false);
        jtomatModel.start();
        start.setText("pause");
        start.setActionCommand("pause");
        useCustomFigureCheckBox.setEnabled(false);
    }

    private void reset() {
        screenButton.setEnabled(false);
        pause();
        jtomatModel.setRule(new Rule(ruleField.getText()));

        if (useCustomFigureCheckBox.isSelected())
            jtomatModel.setStartFigure(figureCanvas.getGridArray());
        else
            jtomatModel.breathOfLife();
        graphPanel.reset();
        start.setActionCommand("resume");
        start.setText("start");
        reset.setEnabled(true);
        useCustomFigureCheckBox.setEnabled(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        Jtomat frame = new Jtomat("Jtomat");
    }
}
