package life.view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Author: Jan Paw
 * Date: 4/12/12
 * Time: 9:59 PM
 */
public class FigurePanel extends JPanel {
    private static final int rowsNumber = 30;
    private static final Dimension size = new Dimension(10, 10);
    protected static final Color selectionColor = new Color(0x000000);
    private Color originalColor;
    private JPanel grid;
    private boolean[] gridArray;

    public FigurePanel() {
        setLayout(new GridLayout(rowsNumber, rowsNumber, 1, 1));
        gridArray = new boolean[rowsNumber * rowsNumber];
        for (int i = 0; i < rowsNumber * rowsNumber; i++) {
            grid = new JPanel();
            grid.setBackground(new Color(0xFFFFFF));
            String name = Integer.toString(i);
            grid.setName(name);
            if (i == 0) {
                originalColor = grid.getBackground();
            }
            grid.setPreferredSize(size);
            add(grid);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JPanel panel = (JPanel) getComponentAt(e.getPoint());
                if (panel == null || panel == FigurePanel.this) {
                    return;
                }
                if (gridArray[Integer.parseInt(panel.getName())]) {
                    gridArray[Integer.parseInt(panel.getName())] = false;
                    panel.setBackground(originalColor);
                    panel.revalidate();
                    panel.repaint();
                } else {
                    gridArray[Integer.parseInt(panel.getName())] = true;
                    panel.setBackground(selectionColor);
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });
    }

    public boolean[] getGridArray() {
        return gridArray;
    }
}
