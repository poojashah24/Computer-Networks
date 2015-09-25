README.txt This file contains the project documentation; program features and
usage scenarios; protocol specification of the implementation, including a complete list of the data messages used, their encodings (syntax) and semantics;

Program Features - 
-------------------------------------
1. The following user commands are supported - 
	a. SHOWRT - Shows the routing table
	b. LINKUP <ipaddress> <port> - Restores the link to the given node
	c. LINKDOWN <ipaddress> <port> - Breaks the link to the given node
	d. CLOSE - Closes the client
	e. HELP (Extra command) - Displays a listing of the commands and their usage.
2. The following messages are exchanged between the nodes internally
	a. LINKUP
	b. LINKDOWN
	c. CLOSE
3. 	Design patterns (command pattern) have been used in order to structure the code efficiently.
4.  Ctrl-C is handled gracefully. The client shuts down, and all its neighbors will mark the link down after their respective timeout period.
5. Nodes can join dynamically as well. Other nodes will include the new node when it sends the 1st ROUTE_UPDATE message.
6. A set of timer tasks have been maintained for each node - 
	a) To broadcast a distance vector to it in case there has been no change since "timeout" seconds. 
	b) To keep track of when the node has received the last update from its neighbor, and to mark the link down after 		3*timeout seconds

Additional Features - 
--------------------------------------
1. Count-to-infinity problem has been avoided i.e. if node A is routing to node C through node B, it will publicize its value to node B as INFINITY.
2. All commands are case-insensitive.
3. Additional help command provided which lists valid command with usage for each.

Protocol Specification -
---------------------------------------
1. The JSON format has been used as the underlying message exchange protocol because it provides good reability, and integrates well with Java

2. The messages have been kept compact, with no unnecessary information included

3. The following messages have been defined - 
	a. ROUTE_UPDATE - The JSON representation includes the command name, distance vector entries as <ipaddress, port, cost> pairs. Additionally, we pass the senders ipaddress/port. On receiving a ROUTE_UPDATE the node will re-compute the distance vector, and if there is a change, will broadcast the new distance vector to its neighbors.
	
	b. LINKUP - The JSON representation inludes the command name, the recipient's ipadress/port and the sender's ipaddress/port. On receiving LINKUP, the receiver node restores the link value for the sender to the value before the link went down. It's distance vector will be re-computed, and if it changes it will sent to all the neighbors

	c. LINKDOWN - The JSON representation inludes the command name, the recipient's ipadress/port and the sender's ipaddress/port. On receiving LINKUP, the receiver node sets the link value for the sender to INFINITY.It's distance vector will be re-computed, and if it changes, it will be sent to all the neighbors.
	

Description of the code
-----------------------
The code has been structured into the following categories - 

Commands - 
--------
1. Command - Abstract command class with the abstract function execute(). All commands derive from this class.
2. CloseCommand - Contains the logic to be executed when CLOSE is invoked
3. HelpCommand - Contains the logic to be executed when HELP is invoked
4. LinkDownCommand - Contains the logic to be executed when LINKDOWN is invoked
5. LinkUpCommand - Contains the logic to be executed when LINKUP is invoked
6. RouteUpdateCommand - Contains the logic to be executed when ROUTE_UPDATE is sent by another client
7. ShowRTCommand - Contains the logic to be executed when SHOWRT is invoked

Main driver classes - 
-------------------
BFClient - This is the entry class. It accepts the input parameters and initializes the node and its neighbor list and distance vector list.  It then settles to accept command line input. It parses the input into commands, and passes them on to be executed.

BFNode - This class initializes the entire state of the client (i.e. neighbors and distance vectors). It also adds the initial timer tasks for each neighbor for sending distance vectors in case of timeout, and setting links down in case of no update from the node.

DVListener - This is a seperate thread that listens for incoming command, and passes them on to be executed

DVSender - This thread is reponsible to broadcast the distance vector at regular intervals (if no change has occurred)

DVSenderUtil - This contains re-usable functions to send the distance vector / linkup / linkdown commands to neighbors.

TimeoutHandler - This is an independent thread that gets triggered when a timeout occurs. It sets the cost to the neighbor as INFINITY, recomputes the distance vector, and incase of a change, sends it to all the neighbors.

Shared state and Distance Vector computation - 
------------
DVState - This encapsulates the shared state across the client i.e. the distance vector list / the neighbor list etc.
Shared as multiple threads need access to it. Protected by synchronization.
This also contains the logic for distance vector computation.

Entities - 
--------
1. DVEntry - Represents a distance vector entry
2. Neighbor - Represents a node who is a neighbor to this client

Common utility files - 
--------------------
1. Constants - Contains reused constant values / error messages
2. IPUtils - Contains utility functions to detect machine's IP address, and generate a unique key based on IP address/port
3. JSONHelper - Contains utility functions to help with JSON serialization / de-serialization
4. ValidationUtils - Contains utility functions to handle validation of IP address/port

Enums - 
-----
CommandEnum - Enum to enlist all the commands
LinkState - Enum to enlist states of a link (UP/DOWN)

Details of development environment
----------------------------------
- The code has been developed in Java, and is compliant with Java version 1.6.
- It does not use any features of Java7/8.
- Eclipse IDE was used as the development/debugging environment.
- A makefile has been placed alongside the source file. The makefile generates class files for all the java files.
- An additional shell script bfClient has been provided to start the client.

Instructions on how to run the code
-----------------------------------
1. Run make to compile the files.

2. There is a shell script "bfClient.sh" that should be invoked with the desired parameters.
Please start the client only by invoking this shell script (not using java <executablename>)
IMPORTANT NOTE - Check that bfClient.sh has execute permissions by running "ls -l" (look for x).
If it does not, please run chmod 755 to assign execute permissions

E.g. ./bfClient.sh 4116 20 160.39.194.219 4115 5.0 [...]
where 	4116 is the client's port
		20 is the timeout for the client
		160.39.194.219 is the neighbor's IP address
		4115 is the neighbor's port number
		5.0 is the link cost to the neighbor
Other neighbors can be added similarly.

