
// When refill_stock belief is added by robot_1 the Stock is refilled 
+refill_stock[source(robot_1)]
	: 	true
	<- 	.print("Refilling stock...");
		.wait(10000); // Wait for 10 seconds
		refill_stock_items;
		.print("Stock refilling complete.");
		-refill_stock[source(robot_1)];
		.send(robot_1, tell, stock_full). // Inform robot_1 that Stock is now full
		

