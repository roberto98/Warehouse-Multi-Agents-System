// Environment code for project progetto_mas.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;
import java.util.logging.*;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class WarehouseEnv extends Environment {

    private Logger logger = Logger.getLogger(WarehouseEnv.class.getName());

    WarehouseModel model;

    @Override
    public void init(String[] args) {
        super.init(args);
        model = new WarehouseModel();

        if (args.length == 1 && args[0].equals("gui")) {
            WarehouseView view  = new WarehouseView(model);
            model.setView(view);
        }
        updatePercepts();
    }

    void updatePercepts() {
        clearPercepts("courier");

        for (RobotAgent robot : model.robots) {
            String robot_name = robot.getName();
            clearPercepts(robot_name);

            Location location = model.getAgPos(robot.getId());

            // Add percept for Stock area
            if (location.equals(model.stock.getLocation())) {
                addPercept(robot.getName(), Literal.parseLiteral("at(" + robot_name + ", stock)"));
            }

            // Add percept for empty Stock
            if(model.stock.isEmpty()){
                addPercept(robot_name, Literal.parseLiteral("empty_stock"));
            }

            // Add percept for Courier area
            if (location.equals(model.courier.getLocation())) {
                addPercept(robot_name, Literal.parseLiteral("at(" + robot_name + ", courier)")); // Add percept to robot
                addPercept(model.courier.getName(), Literal.parseLiteral("at(" + robot_name + ", courier)")); // Add percept to courier
            }

            // Add percept for Charging docker robot 1 area
            if (location.equals(model.l_robot1Docker)) {
                addPercept(robot_name, Literal.parseLiteral("at(" + robot_name + ", docker1)"));
            }

            // Add percept for Charging docker robot 2 area
            if (location.equals(model.l_robot2Docker)) {
                addPercept(robot_name, Literal.parseLiteral("at(" + robot_name + ", docker2)"));
            }

            for (int i = 0; i < model.racks.size(); i++) {
                Rack rack = model.racks.get(i);
                Location rackLocation = rack.getLocation();

                // Add percept for the location of each rack
                if (location.equals(rackLocation)) {
                    addPercept(robot_name, Literal.parseLiteral("at(" + robot_name + "," + rack.getName() + ")"));
                }

                // Add percept to the status of each rack
                if (rack.isEmpty()) {
                    addPercept(robot_name, Literal.parseLiteral("empty("+rack.getName()+")"));
                } else {
                    addPercept(robot_name, Literal.parseLiteral("full("+rack.getName()+")"));
                }
            }
        }
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        //System.out.println("["+agName+"] doing: "+action);
        boolean result = false;
        int agId = -1;

        if(agName.equals("robot_1")){
            agId = 0;
        } else if(agName.equals("robot_2")){
            agId = 1;
        }

        RobotAgent robot = model.getRobotByName(agName);

        String actionName = action.getFunctor();
        String l;
        int n_items;

        // Perform action based on the action name
        switch(actionName) {

            case "move_towards":
                l = action.getTerm(0).toString();
                Location dest = null;

                if (l.equals("stock")) {
                    dest = model.stock.getLocation();
                }

                if (l.equals("courier")) {
                    dest = model.courier.getLocation();
                }

                if (l.equals("docker1")) {
                    dest = model.l_robot1Docker;
                }

                if(l.equals("docker2")){
                    dest = model.l_robot2Docker;
                }

                if (l.startsWith("rack")){
                    Rack emptyRack = model.getRackByName(l);
                    dest = emptyRack.getLocation();
                }

                if (dest != null) {
                    model.moveTowards(agId, dest);
                    updatePercepts();
                    result = true;
                }
                break;

            case "pick_item_from_stock":
                n_items = Integer.parseInt(action.getTerm(0).toString());
                if (!robot.isHoldingItem()) {
                    model.robotPickItemFromStock(robot, model.stock, n_items);
                    updatePercepts();
                    result = true;
                }
                break;

            case "refill_stock_items":
                model.refillStock(model.stock);
                updatePercepts();
                result = true;
                break;

            case "place_item_in_rack":
                n_items = Integer.parseInt(action.getTerm(0).toString());
                l = action.getTerm(1).toString();
                if (l.startsWith("rack")){
                    Rack emptyRack = model.getRackByName(l);
                    if (robot.isHoldingItem()) {
                        model.robotPlaceItemInRack(robot, emptyRack, n_items);
                        updatePercepts();
                        result = true;
                    }
                }
                break;
            case "pick_item_from_rack":
                n_items = Integer.parseInt(action.getTerm(0).toString());
                l = action.getTerm(1).toString();
                if (l.startsWith("rack")){
                    Rack fullRack = model.getRackByName(l);
                    if(!robot.isHoldingItem()){
                        model.robotPickItemFromRack(robot, fullRack, n_items);
                        updatePercepts();
                        result = true;
                    }
                }
                break;

            case "deliver_item_to_courier":
                n_items = Integer.parseInt(action.getTerm(0).toString());
                if(robot.isHoldingItem()){
                    model.courierUpdateCurrentSize(model.courier, n_items);
                    robot.hasDropItem();
                    updatePercepts();
                    result = true;
                }
                break;

            case "courier_check_items":
                model.courierCheckCurrentSize(model.courier);
                updatePercepts();
                result = true;
                break;

            default:
                result = false;
        }
        return result;
    }
}



