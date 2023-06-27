import jason.environment.grid.Location;

public class Courier {

    private final int id;
    private final String name;
    private Location location;
    private int currentSize;
    private final int maxSize = 100;
    private boolean needItems;

    public Courier(int id, Location location, String name) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.currentSize = 0;
        this.needItems = true;
    }

    public int getId() {
        return id;
    }

    public String getName() { return name; }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCurrentSize(int size) {
        this.currentSize = size;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public int getMaxSize() {
        return maxSize;
    }


    public void shippingItems(){ needItems = false; }

    public void askItems(){ needItems = true; }

    public boolean needItems() {
        return needItems;
    }
}