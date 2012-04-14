package life.view;

import life.cell.Cell;
import life.model.JtomatModel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: Jan Paw
 * Date: 4/8/12
 * Time: 7:39 PM
 */
public class CellsPanel extends JPanel implements Observer {
    private LinkedList<Cell> cellsToUpdate;
    private life.cell.Dimension dimension;
    private int cellSize;
    private JtomatModel jtomatModel;
    private CellAreaCanvas cellAreaCanvas;

    public CellsPanel(life.cell.Dimension dimension, final int cellSize, final JtomatModel jtomatModel) {
        this.dimension = new life.cell.Dimension(dimension.getWidth() * cellSize, dimension.getHeight() * cellSize);
        this.cellSize = cellSize;
        this.jtomatModel = jtomatModel;
        jtomatModel.addObserver(this);
        this.setSize(this.dimension.getWidth(), this.dimension.getHeight());
        cellAreaCanvas = new CellAreaCanvas();
        cellAreaCanvas.setSize(this.dimension.getWidth(), this.dimension.getHeight());
        setBackground(new Color(0xf0f1f2));
        this.add(cellAreaCanvas);
    }

    class CellAreaCanvas extends Canvas {
        @Override
        public void paint(Graphics graphics) {
            if (cellsToUpdate != null) {
                for (Cell cell : cellsToUpdate) {
                    if (cell.isValue())
                        if (cell.isUpdated())
                            graphics.setColor(new Color(0x8AE100));
                        else
                            graphics.setColor(new Color(0x000000));
                    else
                        graphics.setColor(new Color(0xFFFFFF));
                    graphics.fillRect(cell.getHeight() * cellSize, cell.getWidth() * cellSize, cellSize, cellSize);
                }
            }
        }

        @Override
        public void update(Graphics g) {
            Graphics offGraphics;
            Image offScreen;
            offScreen = createImage(getWidth(), getHeight());
            offGraphics = offScreen.getGraphics();
            paint(offGraphics);
            g.drawImage(offScreen, 0, 0, this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        cellsToUpdate = (LinkedList<Cell>) arg;
        cellAreaCanvas.repaint();
    }
}

