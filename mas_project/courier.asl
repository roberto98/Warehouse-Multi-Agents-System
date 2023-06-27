
/** Initial beliefs **/
received_items(0).

/** Goals **/
!requestDelivery.

/** Plans **/

// Plan for requesting delivery
+!requestDelivery
    <-  !call_robot;
        !request_items(100).

// Plan for handling the case when 100 items have been received
+received_items(N) : N = 100
    <- !request_items(N).

// Plan when the desired number of items has been received
+!request_items(N_items) 
	: received_items(N_items)
    <- .print("Received ", N_items, " items, happy now!");
        -+received_items(0); // Reset the received items belief to 0
        .wait(10000); // Wait for 10 seconds
        courier_check_items; 
        !requestDelivery. // Request delivery again

// Plan when the desired number of items has not been received
+!request_items(N_items) 
	: not received_items(N_items)
    <-  courier_check_items; 
        .print("I need ", N_items, " items");
        .send(robot_2, tell, need_items(N_items)). // Send a message to robot_2 to request N_items

	
// Plan for handling the case when items are delivered by robot_2
+delivered_items(N)[source(robot_2)]
    <- ?received_items(OldValue);
        -+received_items(OldValue + N); // Update the received items belief with the new delivered items
        .print("Current received_items: ", OldValue + N); 
        -delivered_items(N)[source(robot_2)]. 

// Plan for calling the robot
+!call_robot
    : true 
    <-  .send(robot_2, tell, go_to_courier); // Send a message to robot_2 to go to the courier
        .print("I called the robot!"); 
        !wait_robot. 

// Plans to wait that robot_2 has arrived at the courier
+!wait_robot
    : at(robot_2, courier)
    <- .print("Robot 2 arrived at the courier."). 

+!wait_robot : not at(robot_2, courier)
    <- .print("I'm waiting for the robot...");
        .wait(2000); 
        !wait_robot. // Check the robot's location again

