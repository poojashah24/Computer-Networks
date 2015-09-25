Description of the code
-----------------------
The applications have been designed such that the client is just a thin client i.e. a dumb terminal.
It simply prints out the messages sent by the server and send the commands input by the client to the server.

The server application holds most of the processing logic. It starts up by invoking the command "Server <port>", and listens on the specified port for any incoming connections. On receiving a new connection request, it creates a new socket, and a new thread to handle the interaction with the client (ClientConnectionHandler), and goes back to listening for incoming connections. Thus, in addition to the server application, we have one thread per incoming connection request.

The thread is now responsible for all interaction with the client. It authenticates the client, accepts commands, executes them, and returns the output to the client. In case of multiple clients, its also routes the requests from one client to another.

A connection can be shut down in several ways - 
1) Client logs out
2) Server shuts down
3) Client times out

In each case, the server send an appropriate message to the client before cleanly exiting.

Code structure:-
- The code consists of a client part and a server part.
- All the source code files are placed together.
- The Client code consists of 2 files - Client and Reader. Client establishes the connection with the server, and listens for user input, while Reader is an additional thread that listens for data from the server (on the socket connection)
- The Server code consists of several files, briefly described below - 

1) Server.java - This is the main server application. It listens for connections, and then delegates to a new thread.

2) ClientConnectionHandler.java - This is the thread that interacts with the client. It authenticates the client, received commands, and sends messages from other users to the client.

3) BlockedConnectionCleanupTask - This is a timer used in order to reset a connection once it has been blocked i.e. once a user connecting from a specific IP has 3 consecutive invalid login attempts, further logins from that user/IP are blocked for a specific duration of time (which is configurable). This task runs when the duration has completed, and "unblocks" the user/IP again.

4) ChatUser/Command/State/Presence/OfflineMessage - These classes represent objects/enums that are used in the business logic.


NOTE - All the constants for TIME_OUT, LAST_HOUR, BLOCK_TIME and  are defined in the file Constants.java.

Details of development environment
----------------------------------
- The code has been developed in Java, and is compliant with Java version 1.6.
- It does not use any features of Java7/8.
- Eclipse IDE was used as the development/debugging environment.
- A makefile has been placed alongside the source file. The makefile generates class files for all the java files.
- An additional file user_pass.txt has been used in order to store the combination of usernames and passwords.

Instructions on how to run the code
-----------------------------------
- There are 2 applications to be run - Server and Client
- The Server can be started by invoking the command
"java Server <port>"
-The Client can be started by invoking the command
"java Client <ip> <port>", where ip, port are the ipaddress and port of the server resp.

NOTE - All the constants for TIME_OUT, LAST_HOUR, BLOCK_TIME and  are defined in the file Constants.java.

Sample commands to invoke the code
----------------------------------
Some of the basic commands that can be invoked on the Client program are - 
1) logout - This logs the user out
2) whoelse - This prints a list of all other users online
3) wholasthr - This prints a list of all the users who are/were online since the last 1 hour
4) broadcast <message> - This sends a message to all the users currently online
5) msg <user> <message> - This sends a private message to a user

Additional functionalities
--------------------------
1) Offline messaging - If a message is sent to an offline user, it is stored and displayed to the user when he/she comes online.
NOTE - This requires the user to have logged in to the chat server previously atleast once.

2) Presence - Each user can update his/her presence to one of the valid presence values (available, busy, away, donotdisturb). A user in donotdisturb mode will not receive any messages.
The command for this is "presence <state>"

3) Tag / Untag User - A user can tag/untag any other user. If User A tags User B, each time User B changes presence, User A will receive a notification of the status change, and the new status. After untagging, User A will stop receiving notifications.
The command for this is "tag <user>" or "untag <user>"

4) Block/Unblock user - A user can block or unblock another user. A blocked user can neither send messages nor tag the user (who has blocked him/her).
The command for this is "block <user>" or "unblock <user>"

5) Help command - A user can invoke the "help" command to get a list of supported functionalities and their usage.
