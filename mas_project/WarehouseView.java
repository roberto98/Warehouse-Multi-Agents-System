import jason.environment.grid.*;

import java.awt.Color;
/*
    black
	blue
	cyan
	darkGray
	gray
	green
	lightGray
	magenta
	orange
	pink
	red
	white
	yellow
*/

import java.awt.Font;
import java.awt.Graphics;


/** class that implements the View of Warehouse application */
public class WarehouseView extends GridWorldView {

    WarehouseModel wmodel;

    public WarehouseView(WarehouseModel model) {
        super(model, "Smart Warehouse Simulation", 1000);
        wmodel = model;

        defaultFont = new Font("Arial", Font.BOLD, 13); // change default font
        setVisible(true);
        repaint();
    }

    /** draw applications objects **/
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        if (object == wmodel.stock.getId()) {
            drawStock(g, x, y);
        } else if (object == wmodel.courier.getId()) {
            drawCourier(g, x, y);
        } else if (object == wmodel.robot1DockLocation || object == wmodel.robot2DockLocation){
            drawDockLoc(g, x, y, object);
        } else {
            drawRacks(g, x, y, object);
        }
    }


    /** draw applications agents **/
    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {

        String label = "R"+(id+1);

        // Set the color for the drawAgent
        if (id == 0) {
            c = Color.red;
            if(wmodel.robot1.isHoldingItem()){
                c = Color.lightGray;
            }
        } else if (id == 1) {
            c = Color.green;
            if(wmodel.robot2.isHoldingItem()){
                c = Color.lightGray;
            }
        }

        super.drawAgent(g, x, y, c, -1);

        // Set the color for the string on the Agent
        if (id == 0 || id == 1) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.black);
        }
        super.drawString(g, x, y, defaultFont, label);
    }

    /** Draw functions for every object **/
    private void drawStock(Graphics g, int x, int y) {
        Color c = Color.blue;
        super.drawAgent(g, x, y, c, -1);
        g.setColor(Color.white);
        drawString(g, x, y, defaultFont, "Stock(" + wmodel.stock.getCurrentSize() + ")");
    }

    private void drawCourier(Graphics g, int x, int y) {
        Color c = Color.blue;
        if (!wmodel.courier.needItems()) {
            c = Color.cyan;
        }

        super.drawAgent(g, x, y, c, -1);
        g.setColor(Color.white);
        drawString(g, x, y, defaultFont, "Courier "+wmodel.courier.getCurrentSize() +"/100");
        //repaint();
    }

    private void drawDockLoc(Graphics g, int x, int y, int dockId) {
        int id = -1;
        Color c = Color.black;

        if(dockId == 2048){
            id = 1;
            c = Color.red;
        } else if(dockId == 4096){
            id = 2;
            c = Color.green;
        }

        super.drawAgent(g, x, y, c, -1);
        g.setColor(Color.black);
        drawString(g, x, y, defaultFont, "Docker "+id);
    }

    private void drawRacks(Graphics g, int x, int y, int rackId) {
        Rack rack = wmodel.getRackById(rackId);

        if (rack != null) {
            super.drawAgent(g, x, y, Color.orange, -1);
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "Rack (" + rack.getCurrentSize() + ")");
        }
    }


}