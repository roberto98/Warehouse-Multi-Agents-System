import jason.environment.grid.Location;


public class RobotAgent {

    private final int id;
    private final String name;
    private Location location;
    private boolean holdingItem;

    /*
    private int batteryLevel;
    private String chargingDockLocation;
    private Map<String, Integer> inventory;
*/
    public RobotAgent(int id, Location location, String name) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.holdingItem = false;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void hasPickItem() {
        holdingItem = true;
    }

    public void hasDropItem() {
        holdingItem = false;
    }

    public boolean isHoldingItem() {
        return holdingItem;
    }


}

