import jason.environment.grid.Location;

public class Rack {
    private int id;
    private String name;
    private final int maxSize = 50;
    private int currentSize;
    private Location location;

    public Rack(int id, String name, int currentSize, Location location) {
        this.id = id;
        this.name = name;
        this.currentSize = currentSize;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
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
        if(this.currentSize < maxSize)
            return true;
        return false;
    }
}
