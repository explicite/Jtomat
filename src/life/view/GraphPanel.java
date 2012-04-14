package life.view;

import life.model.JtomatModel;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: Jan Paw
 * Date: 4/10/12
 * Time: 5:01 PM
 */
public class GraphPanel extends JPanel implements Observer {
    private double[] graphUpdateSize;
    private double[] graphPopulationSize;
    private double updateSize = 0;
    private double populationSize = 0;
    private life.cell.Dimension dimension = new life.cell.Dimension(300, 120);
    private GraphCanvas graphCanvas;
    private JtomatModel jtomatModel;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public GraphPanel() {
        this.graphUpdateSize = new double[100];
        this.graphPopulationSize = new double[100];
        for (int i = 0; i < graphUpdateSize.length; i++) {
            graphUpdateSize[i] = -1;
            graphPopulationSize[i] = -1;
        }
        this.setSize(this.dimension.getWidth(), this.dimension.getHeight());
        setBackground(new Color(0xFFFFFF));
        graphCanvas = new GraphCanvas();
        graphCanvas.setSize(this.dimension.getWidth(), this.dimension.getHeight());
        this.add(graphCanvas);
    }

    public void setJtomatModel(JtomatModel jtomatModel) {
        this.jtomatModel = jtomatModel;
        jtomatModel.addObserver(this);
    }

    class GraphCanvas extends Canvas {
        @Override
        public void paint(Graphics graphics) {
            populationSize = Math.abs(graphPopulationSize[0] - 99.99);
            updateSize = Math.abs(graphUpdateSize[0] - 99.99);

            graphics.setColor(new Color(0xD7D7D7));
            graphics.drawLine(0, 0, 300, 0);
            graphics.drawLine(0, 49, 300, 49);
            graphics.drawLine(0, 99, 300, 99);
            graphics.setColor(new Color(0xF5F5F5));
            graphics.drawLine(0, 24, 300, 24);
            graphics.drawLine(0, 73, 300, 73);
            graphics.setColor(new Color(0x8AE100));
            graphics.fillRect(10, 110, 6, 6);
            for (int i = 0; i < graphUpdateSize.length - 1; i++)
                if (graphUpdateSize[i] > -1 && graphUpdateSize[i + 1] > -1)
                    graphics.drawLine(300 - (i * 3), (int) graphUpdateSize[i], 300 - ((i + 1) * 3),
                            (int) graphUpdateSize[i + 1]);

            graphics.setColor(new Color(0xE10400));
            graphics.fillRect(140, 110, 6, 6);
            for (int i = 0; i < graphPopulationSize.length - 1; i++)
                if (graphPopulationSize[i] > -1 && graphPopulationSize[i + 1] > -1)
                    graphics.drawLine(300 - (i * 3), (int) graphPopulationSize[i], 300 - ((i + 1) * 3),
                            (int) graphPopulationSize[i + 1]);

            graphics.setColor(new Color(0x000000));
            graphics.drawString(" updated cells " + decimalFormat.format(updateSize) + "%", 17, 116);
            graphics.drawString(" population size " + decimalFormat.format(populationSize) + "%", 147, 116);
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

    private void addUpdateSizeToGraph(int value) {
        for (int i = graphUpdateSize.length - 2; i >= 0; i--)
            graphUpdateSize[i + 1] = graphUpdateSize[i];

        graphUpdateSize[0] = 99.99 - ((double) value / (jtomatModel.getPopulationSize()) * 100);
    }

    private void addPopulationPoint(int value) {
        for (int i = graphPopulationSize.length - 2; i >= 0; i--)
            graphPopulationSize[i + 1] = graphPopulationSize[i];

        graphPopulationSize[0] = 99.99 - ((double) value / jtomatModel.getAreaSize() * 100);
    }

    public void reset() {
        this.graphUpdateSize = new double[100];
        this.graphPopulationSize = new double[100];
        graphUpdateSize[0] = 99.99 - ((double) jtomatModel.getUpdateSize() / (jtomatModel.getPopulationSize()) * 100);
        graphPopulationSize[0] = 99.99 - ((double) jtomatModel.getPopulationSize() / jtomatModel.getAreaSize() * 100);
        for (int i = 1; i < graphUpdateSize.length; i++) {
            graphUpdateSize[i] = -1;
            graphPopulationSize[i] = -1;
        }
        graphCanvas.repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        addUpdateSizeToGraph(jtomatModel.getUpdateSize());
        addPopulationPoint(jtomatModel.getPopulationSize());
        graphCanvas.repaint();
    }
}
