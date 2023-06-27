
/** Initial beliefs **/
battery_level(300).
picked_items(0).

/** Rules **/
has_enough_battery :- battery_level(Level) & Level > 20.

/** Goals **/
!fill_rack.

/** Plans **/
+!fill_rack
  <- !move_to(stock);
     !pick_items_from_stock(10);
     !bring_items_to_rack(N);
     !fill_rack.

// Plan for Pick items from the Stock
+!pick_items_from_stock(N)
    : at(robot_1, stock)[source(percept)] & not empty_stock[source(percept)]
    <- .print("Picking ", N, " items from stock");
       pick_item_from_stock(N);
       -+picked_items(N); // Update the picked_items belief
       .wait(1000).
	   
// Plan for when Stock is empty
+!pick_items_from_stock(N)
    : at(robot_1, stock)[source(percept)] & empty_stock[source(percept)]
    <- .print("Stock is empty, waiting...");
	   .send(stock, tell, refill_stock); // Send message to stock to refill it
	   .wait({+stock_full[source(stock)]}); // Wait for the stock to be refilled
	   	-stock_full[source(stock)];
		!pick_items_from_stock(N). // Pick items again
	 
// Plan for bringing items to the rack
+!bring_items_to_rack(N)
    : picked_items(N)[source(self)]
    <- !check_for_empty_rack;
	   ?empty(Rack); // Query for empty rack
	   .print("Delivering ", N, " items at ", Rack);
       !move_to(Rack);
	   !put_items_to_rack(N, Rack).

// Plan for handling case when robot hasn't picked items
+!bring_items_to_rack(N, Rack)
    : not picked_items(N)[source(self)]
    <- .print("I haven't picked up the ", N, " items");
       !fill_rack.

// Plans for check if there's an empty rack
 +!check_for_empty_rack : empty(Rack)[source(percept)] <- true.
 
+!check_for_empty_rack
	: not empty(Rack)[source(percept)] 
	<- .print("No empty rack found, waiting...");
  	   !move_to(docker1);
       .wait(5000); // Wait for 5 seconds
       !check_for_empty_rack. // Check for empty rack again
	
// Plan for putting items in the rack
+!put_items_to_rack(N, Rack)
    : at(robot_1, Rack)[source(percept)] & empty(Rack)[source(percept)]
    <- place_item_in_rack(N, Rack);
	   -picked_items(_); // Reset the picked_items belief
	   .print("Dropped the ", N, " items at ", Rack);
       .wait(1000).

	   
/* Movement function */
+!move_to(Loc) : has_enough_battery
  <- !at(robot_1, Loc);
     -battery_level(Level);
     +battery_level(Level - 10); // Update battery level after moving
	 ?battery_level(Value); // Query the updated battery level
	 .print("Battery level: ", Value).

+!move_to(Loc) : not has_enough_battery
  <- !return_to_charging_dock;
     !wait_until_charged;
     !move_to(Loc).
	 
+!at(robot_1, Place) : at(robot_1, Place) <- true.

+!at(robot_1, Place)
    : not at(robot_1,Place)
    <- move_towards(Place);
       .wait(100);
       !at(robot_1, Place).
	   
+!return_to_charging_dock : true
  <- .print("Battery low, I go to charging dock.");
  	 !at(robot_1, docker1).

+!wait_until_charged : true
  <- .wait(10000); // Wait for 10 seconds
     -battery_level(Level);
     +battery_level(300); // Assume the battery is fully charged after waiting
	 .print("Battery recharged. I'm ready").
