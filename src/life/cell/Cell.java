package life.cell;

/**
 * Author: Jan Paw
 * Date: 4/8/12
 * Time: 8:20 PM
 */
public class Cell {
    private boolean value;
    private boolean updated;
    private int width;
    private int height;

    public Cell(boolean value, boolean updated, int width, int height) {
        this.value = value;
        this.width = width;
        this.height = height;
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public boolean isValue() {
        return value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
