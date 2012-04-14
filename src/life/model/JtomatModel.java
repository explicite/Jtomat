package life.model;

import life.cell.Cell;
import life.cell.Dimension;
import life.rule.Rule;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Author: Jan Paw
 * Date: 4/9/12
 * Time: 1:47 PM
 */
public class JtomatModel extends Observable {
    private boolean[][] area;
    private boolean[][] nextArea;
    private Dimension dimension;
    private volatile int updateSize = 1;
    private volatile int populationSize = 1;
    private int stepTime = 0;
    private final ModelThread modelThread;
    private Rule rule;


    public JtomatModel(Dimension dimension, int stepTime) {
        this.dimension = dimension;
        this.area = new boolean[dimension.getHeight()][dimension.getWidth()];
        this.nextArea = new boolean[dimension.getHeight()][dimension.getWidth()];
        this.stepTime = stepTime;
        this.modelThread = new ModelThread();
        modelThread.start();
    }

    private void nextStep() throws InterruptedException {
        LinkedList<Cell> cells = new LinkedList<Cell>();
        boolean updateFlag;
        updateSize = 0;
        populationSize = 0;
        for (int i = 0; i < dimension.getHeight(); i++) {
            for (int j = 0; j < dimension.getWidth(); j++) {
                updateFlag = false;
                if (updatedCell(i, j)) {
                    updateFlag = true;
                    nextArea[i][j] = !area[i][j];
                    if (nextArea[i][j])
                        updateSize++;
                }
                cells.add(new Cell(nextArea[i][j], updateFlag, i, j));
                if (nextArea[i][j])
                    populationSize++;
            }
        }

        for (int i = 0; i < dimension.getHeight(); i++)
            System.arraycopy(nextArea[i], 0, area[i], 0, dimension.getWidth());

        if (!cells.isEmpty()) {
            sendUpdate(cells);
        } else stop();
    }

    public int getPopulationSize() {
        return populationSize > 0 ? populationSize : 1;
    }

    public int getAreaSize() {
        return dimension.getHeight() * dimension.getWidth();
    }

    public int getUpdateSize() {
        return updateSize > 0 ? updateSize : 1;
    }

    public void breathOfLife() {
        Random random = new Random(System.currentTimeMillis());
        LinkedList<Cell> cells = new LinkedList<Cell>();
        updateSize = 0;
        populationSize = 0;
        for (int i = 0; i < dimension.getHeight(); i++)
            for (int j = 0; j < dimension.getWidth(); j++) {
                area[i][j] = false;
                nextArea[i][j] = false;
                if (random.nextGaussian() > 0.5) {
                    area[i][j] = true;
                    nextArea[i][j] = true;
                    updateSize++;
                }
                cells.add(new Cell(area[i][j], true, i, j));
            }
        populationSize = updateSize;
        sendUpdate(cells);
    }

    public void setStartFigure(boolean[] cellsArray) {
        LinkedList<Cell> cells = new LinkedList<Cell>();
        updateSize = 0;

        for (int i = 0; i < dimension.getHeight(); i++) {
            for (int j = 0; j < dimension.getWidth(); j++) {
                area[i][j] = false;
                nextArea[i][j] = false;
            }
        }
        int z = 0;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                area[i + 135][j + 135] = cellsArray[z];
                nextArea[i + 135][j + 135] = cellsArray[z];
                if (cellsArray[z])
                    updateSize++;
                z++;
            }
        }

        populationSize = updateSize;
        for (int i = 0; i < dimension.getHeight(); i++) {
            for (int j = 0; j < dimension.getWidth(); j++) {
                cells.add(new Cell(area[i][j], true, i, j));
            }
        }
        sendUpdate(cells);
    }

    private boolean updatedCell(int x, int y) {
        boolean updateCell = false;

        if (area[x][y] && !rule.getRule(countNeighbors(x, y)))
            updateCell = true;

        if (!area[x][y] && rule.getRule(countNeighbors(x, y) + 9))
            updateCell = true;

        return updateCell;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    private int countNeighbors(int x, int y) {
        boolean[] neighbors = new boolean[8];

        neighbors[0] = area[(((x - 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y - 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[1] = area[(((x - 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[2] = area[(((x - 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y + 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[3] = area[(((x) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y - 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[4] = area[(((x) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y + 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[5] = area[(((x + 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y - 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[6] = area[(((x + 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];

        neighbors[7] = area[(((x + 1) % dimension.getHeight()) + dimension.getHeight()) % dimension.getHeight()]
                [(((y + 1) % dimension.getWidth()) + dimension.getWidth()) % dimension.getWidth()];


        int numberOfNeighbors = 0;
        for (boolean neighbor : neighbors)
            if (neighbor) numberOfNeighbors++;

        return numberOfNeighbors;
    }

    public void start() {
        synchronized (modelThread) {
            modelThread.count = true;
        }
    }

    public void stop() throws InterruptedException {
        synchronized (modelThread) {
            modelThread.count = false;
        }
    }

    private void sendUpdate(LinkedList<Cell> cells) {
        setChanged();
        notifyObservers(cells);
    }

    class ModelThread extends Thread {
        public volatile boolean count = false;

        @Override
        public void run() {
            while (true) {
                while (count) {
                    try {
                        long start = System.currentTimeMillis();
                        nextStep();
                        long stop = System.currentTimeMillis();
                        TimeUnit.MILLISECONDS.sleep((stepTime < (stop - start)) ? 0 : stepTime - (stop - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
