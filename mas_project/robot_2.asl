
/** Initial beliefs **/
battery_level(100).
picked_items(0).

/** Rules **/
has_enough_battery :- battery_level(Level) & Level > 20.

/** Plans **/
// Plan for going to the courier when requested
+go_to_courier[source(courier)]
	: true
	<- !move_to(courier); 
		-go_to_courier[source(courier)].

// Plan for handling the courier's need for items
+need_items(N)[source(courier)]
  : true
  <- .print("The courier needs ", N, " items");
     !handle_courier_request(N);
     -need_items(N)[source(courier)].

// Plan for handling courier requests
+!handle_courier_request(N) 
	: 	N > 0
	<- 	!check_for_full_rack;
		?full(Rack); // Query for full rack
		.print("Rack ", Rack, " is full. I'm going to it");
		!move_to(Rack);
		!pick_items_from_rack(50, Rack);
		!deliver_items_to_courier(50);
		.send(courier, tell, delivered_items(50)); // Send message to courier that items have been delivered
		!handle_courier_request(N - 50). // Handle the remaining courier request

// Plan for handling completed courier requests
+!handle_courier_request(N)
	: N <= 0
	<- .print("Courier request completed, I go to docker");
		!move_to(docker2).

// Plans to check if there are full racks
+!check_for_full_rack : full(Rack)[source(percept)] <- true.

+!check_for_full_rack
	: not full(Rack)[source(percept)] 
	<- .print("No full rack found, waiting...");
  		!move_to(docker2);
		.wait(5000); // Wait for 5 seconds
		!check_for_full_rack. // Check for full rack again
	
// Plan for picking items from a rack
+!pick_items_from_rack(N, Rack)
	: at(robot_2, Rack)[source(percept)] & full(Rack)[source(percept)]
	<- .print("Picking up ", N, " items from ", Rack);
		pick_item_from_rack(N, Rack);
	    -+picked_items(N); // Update the picked_items belief
     	.wait(1000).

// Plan for delivering items to the courier
+!deliver_items_to_courier(N) 
    : picked_items(N)[source(self)] // Robot has picked N items
	<- .print("Delivering ", N, " items to courier");
		!move_to(courier);
		deliver_item_to_courier(N); 
		-picked_items(_); // Reset the picked_items belief
		.wait(1000).

/** Movement functions **/
		
+!move_to(Loc) : has_enough_battery
  <- !at(robot_2, Loc);
     -battery_level(Level);
     +battery_level(Level - 10); // Update battery level after moving
	 ?battery_level(Value);
	 .print("Battery level: ", Value).

+!move_to(Loc) : not has_enough_battery
  <- !return_to_charging_dock;
     !wait_until_charged;
     !move_to(Loc).
	 
+!at(robot_2,Place)
	:	at(robot_2,Place)
	<-	true.
	
+!at(robot_2,Place)
	:	not at(robot_2,Place)
	<-	move_towards(Place);
		.wait(800);
		!at(robot_2,Place).

+!return_to_charging_dock : true
  <- .print("Battery low, I go to charging dock.");
  	 !at(robot_2, docker2).

+!wait_until_charged : true
  <- .wait(10000); // Wait for 10 seconds
     -battery_level(Level);
     +battery_level(100); // Assume the battery is fully charged after waiting
	 .print("Battery recharged. I'm ready").

