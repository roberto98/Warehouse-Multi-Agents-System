import jason.environment.grid.Location;

public class Stock {
    private int id;
    private int maxSize = 100;
    private int currentSize;
    private Location location;

    public Stock(int id, Location location) {
        this.id = id;
        this.location = location;
        this.currentSize = maxSize;
    }

    public int getId() {
        return id;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setCurrentSize(int size) {
        this.currentSize = size;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public boolean isEmpty() {
        if(this.currentSize <= 0)
            return true;
        return false;
    }
}
