import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


// https://jason.sourceforge.net/api/

public class WarehouseModel extends GridWorldModel {

    // the grid size
    public static final int GSize = 15;

    public static int robot1DockLocation = 2048;
    Location l_robot1Docker = new Location(7, GSize-1);

    public static int robot2DockLocation = 4096;
    Location l_robot2Docker = new Location(7,0);


    public RobotAgent robot1, robot2;
    public List<RobotAgent> robots;

    public Rack rack1, rack2, rack3;
    public Rack rack4, rack5, rack6;
    public List<Rack> racks;

    public Stock stock;
    public Courier courier;

    public WarehouseModel() {
        // create a GSize x GSize grid with two mobile agents
        super(GSize, GSize, 2);

        // Initialize the robots
        robot1 = new RobotAgent(0, new Location(0, 0), "robot_1");
        robot2 = new RobotAgent(1, new Location(GSize-1, GSize-1), "robot_2");

        robots = new ArrayList<>();
        robots.add(robot1);
        robots.add(robot2);

        //Initialize the racks
        rack1 = new Rack(8, "rack1", 50, new Location(4,5));
        rack2 = new Rack(16, "rack2", 0, new Location(7,5));
        rack3 = new Rack(32, "rack3", 50, new Location(10,5));
        rack4 = new Rack(64, "rack4", 0, new Location(4,9));
        rack5 = new Rack(128, "rack5", 50, new Location(7,9));
        rack6 = new Rack(256, "rack6", 0, new Location(10,9));

        racks = new ArrayList<>();
        racks.add(rack1);
        racks.add(rack2);
        racks.add(rack3);
        racks.add(rack4);
        racks.add(rack5);
        racks.add(rack6);

        //Initialize the stock and the courier
        stock = new Stock(512, new Location(0, GSize-1));
        courier = new Courier(1024, new Location(GSize-1, 0), "courier");

        // initial location of robot
        setAgPos(robot1.getId(), robot1.getLocation().x, robot1.getLocation().y);
        setAgPos(robot2.getId(), robot2.getLocation().x, robot2.getLocation().y);
        //setAgPos(courier.getId(), courier.getLocation().x, courier.getLocation().y);

        // initial location of the bases
        add(stock.getId(), stock.getLocation().x, stock.getLocation().y);
        add(courier.getId(), courier.getLocation().x, courier.getLocation().y);
        add(robot1DockLocation, l_robot1Docker);
        add(robot2DockLocation, l_robot2Docker);


        // initial location of racks
        add(rack1.getId(), rack1.getLocation().x, rack1.getLocation().y);
        add(rack2.getId(), rack2.getLocation().x, rack2.getLocation().y);
        add(rack3.getId(), rack3.getLocation().x, rack3.getLocation().y);
        add(rack4.getId(), rack4.getLocation().x, rack4.getLocation().y);
        add(rack5.getId(), rack5.getLocation().x, rack5.getLocation().y);
        add(rack6.getId(), rack6.getLocation().x, rack6.getLocation().y);

    }

    boolean moveTowards(int agId, Location dest) {
        Location r = getAgPos(agId);
        if (r.x < dest.x)        r.x++;
        else if (r.x > dest.x)   r.x--;
        if (r.y < dest.y)        r.y++;
        else if (r.y > dest.y)   r.y--;
        setAgPos(agId, r); // move the robot in the grid

        return true;
    }

    public void robotPickItemFromStock(RobotAgent robot, Stock stock, int n){
        if (stock.getCurrentSize() >= n) {
            stock.setCurrentSize(stock.getCurrentSize() - n);
            robot.hasPickItem();
        } else {
            System.out.println("Stock is empty. Wait for refill.");
            stock.setCurrentSize(0);
        }
    }

    public void refillStock(Stock stock){
        if(stock.getCurrentSize() <= 0){
            stock.setCurrentSize(stock.getMaxSize());
        } else {
            System.out.println("Stock is not empty. No need to refill.");
        }
    }

    public void robotPickItemFromRack(RobotAgent robot, Rack rack, int n){
        if (rack.getCurrentSize() >= n) {
            rack.setCurrentSize(rack.getCurrentSize() - n);
            robot.hasPickItem();
        } else {
            System.out.println("Rack is empty. Wait for refill.");
            rack.setCurrentSize(0);
        }
    }


    public void robotPlaceItemInRack(RobotAgent robot, Rack rack, int n) {
        if (rack.getCurrentSize() <= rack.getMaxSize()) {
            rack.setCurrentSize(rack.getCurrentSize() + n);
            robot.hasDropItem();
        } else {
            System.out.println("Rack is full. Cannot place item.");
        }
    }

    public void courierUpdateCurrentSize(Courier courier, int n) {
        if (courier.getCurrentSize() <= courier.getMaxSize()) {
            courier.setCurrentSize(courier.getCurrentSize() + n);
        } else {
            System.out.println("Courier is full of items. Wait the shipping.");
        }
    }

    public void courierCheckCurrentSize(Courier courier){
        if (courier.getCurrentSize() >= courier.getMaxSize()) {
            courier.shippingItems();
            courier.setCurrentSize(0);
        } else {
            courier.askItems();
        }
    }

    public Rack getRackById(int id) {
        for (Rack rack : racks) {
            if (rack.getId() == id) {
                return rack;
            }
        }
        return null; // if no rack with that id is found
    }

    public Rack getRackByName(String name) {
        for (Rack rack : racks) {
            if (rack.getName().equals(name)) {
                return rack;
            }
        }
        return null;
    }

    public RobotAgent getRobotById(int id) {
        for (RobotAgent robot : robots) {
            if (robot.getId() == id) {
                return robot;
            }
        }
        return null;
    }

    public RobotAgent getRobotByName(String name) {
        for (RobotAgent robot : robots) {
            if (robot.getName().equals(name)) {
                return robot;
            }
        }
        return null;
    }


}
