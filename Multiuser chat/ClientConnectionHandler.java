import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * Contains the business logic to interact with clients and execute commands.
 * @author Pooja
 *
 */
public class ClientConnectionHandler implements Runnable {

	private Socket socket;
	private String userName;
	private ChatUser user;
	private PrintWriter out;
	
	private static boolean isStopped;
	
	//Map of username/password combinations
	private static HashMap<String, String> credentials = new HashMap<String, String>();
	
	//Map of usernames, and the chat users corresponding to them
	private static HashMap<String, ChatUser> connections;
	
	//Map of connections that have been blocked
	private static HashMap<String, List<String>> blockedConnections;

	static {
		try {
			isStopped = false;
			connections = new HashMap<String, ChatUser>();
			blockedConnections = new HashMap<String, List<String>>();
			
			//Read the file in which the user credentials are stored, and cache them.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(Constants.CREDENTIALS_FILE)));
			String buf;
			while ((buf = reader.readLine()) != null) {
				String[] creds = buf.split(" ");
				credentials.put(creds[0], creds[1]);
			}
			reader.close();
		} catch (Exception e) {
			System.err
					.println("Exception occurred while fetching credentials: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	public ClientConnectionHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			String uName = new String();
			String currentUserName = null;
			boolean loginSuccess = false;
			int counter = 0;
			out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			//Perform authentication. After 3 consecutive invalid login attempts, block the user/IP combination.
			while (counter < 3) {
				
				if(uName != null)
					currentUserName = new String(uName);
				getInput(Constants.LOGIN_PROMPT);
				uName = reader.readLine();
				
				if(uName != null && uName.equals(currentUserName))
					counter++;
				else
					counter = 1;

				getInput(Constants.PASSWORD_PROMPT);
				String pwd = reader.readLine();

				//If a user tries to connect while the connection is blocked, display an error message.
				List<String> ips = blockedConnections.get(uName);
				if (ips != null) {
					for (String i : ips) {
						if (socket.getInetAddress().getHostAddress().equals(i)) {
							out.println(Constants.ACCESS_DISABLED);
							out.flush();
							socket.close();
							return;
						}
					}
				}

				String pass = credentials.get(uName);
				if (pass == null || pass.isEmpty() || !pass.equals(pwd)) {
					handleLogin(Constants.INVALID_LOGIN_DETAILS);
				} else {
					ChatUser c = connections.get(uName);
					// This does not count as a failed login attempt
					// We decrement the counter?
					if (c != null && c.getState().equals(State.loggedIn)) {
						counter--;
						handleLogin(Constants.DUPLICATE_LOGIN);
					} else if (pass.equals(pwd)) {
						//The user has logged in successfully. Setup user data
						loginSuccess = true;
						handleLogin(Constants.WELCOME_PROMPT);
						userName = uName;
						ChatUser chatUser = connections.get(uName);
						ChatUser newUser;
						if(connections.get(uName) == null)
						{
							newUser = new ChatUser(userName, socket, reader,
									out);
						}
						else
						{
							newUser = new ChatUser(userName, socket,
									reader, out, chatUser.getOfflineMessages());
						}
						
						//Register the user in the connections map
						synchronized (ClientConnectionHandler.class) {
							connections.put(userName, newUser);
						}
						user = newUser;
						break;
					}
				}
			}

			//3 invalid attempts. Block user.
			if (counter >= 3 && !loginSuccess) {
				blockUser(uName);
				reader.close();
				out.close();
				socket.close();
				return;
			}
			
			//Read and send offline messages (if any) to the user
			readOfflineMessages();
				
			String buf;
			boolean loggedOut = false;
			Command command;
			
			socket.setSoTimeout(Constants.TIME_OUT);
			while ((buf = reader.readLine()) != null) {

				String[] args = buf.split(Constants.SPACE, 2);
				command = getCommand(args[0]);
				
				user = null;
				switch (command) {
				case HELP:
					handleHelp();
					break;
					
				case LOGOUT:
					handleLogout();
					loggedOut = true;
					break;

				case WHOELSE:
					handleWhoElse();
					break;

				case WHOLASTHR:
					handleWhoLastHr();
					break;

				case BROADCAST:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String msg = args[1];
						handleBroadCast(msg);
					}	
					break;

				case MESSAGE:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String[] params = args[1].split(Constants.SPACE, 2);
						if(params.length != 2)
						{
							printMessage(Constants.INCORRECT_COMMAND);
						}
						else
						{
							String userToMsg = params[0];
							String msgToSend = params[1];
							handleMessage(userToMsg, msgToSend);
						}
					}
					
					break;
					
				case BLOCK:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String userToBlock = args[1];
						handleBlockUser(userToBlock);
					}	
					break;
					
				case UNBLOCK:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String userToUnblock = args[1];
						handleUnblockUser(userToUnblock);
					}
					break;
					
				case PRESENCE:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String status = args[1];
						handlePresenceChange(status);
					}
					break;
					
				case TAG:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String userToTag  = args[1];
						handleTagUser(userToTag);
					}
					break;
					
				case UNTAG:
					if(args == null || args.length != 2)
					{
						printMessage(Constants.INCORRECT_COMMAND);
					}
					else
					{
						String userToUntag  = args[1];
						handleUntagUser(userToUntag);
					}
					break;

				case UNKNOWN:
					handleUnknownCommand(args[0]);
					break;
				}
				if (loggedOut)
					break;
			}

			if (loggedOut) {
				reader.close();
				out.close();
				socket.close();
			}
		} catch (SocketTimeoutException ste) {
			printMessage(Constants.SESSION_EXPIRED);
			printNextPrompt();
			out.close();
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("IOException occurred while closing socket:"
						+ e.getMessage());
				e.printStackTrace();
			}

		} 
		catch (SocketException se)
		{
			if(!isStopped)
			{
				System.err.println("Socket Exception occurred in ClientConnectionHandler run():");
				se.printStackTrace();
			}
		}
		catch (Exception e) {
			
			  System.err.println("Exception occurred in ClientConnectionHandler run():");
			  e.printStackTrace();
		}
		connections.get(userName).setState(State.loggedOut);
	}
		
	//Get command enum from user input
	private Command getCommand(String cmd)
	{
		Command command = null;
		try
		{
			command = Command.valueOf(cmd.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(String.format(Constants.INVALID_COMMAND, cmd));
			command = Command.UNKNOWN;
		}
		return command;
	}
	
	private void handleLogin(String message)
	{
		printMessage(message);
	}
	
	//Block the user after 3 invalid login attempts
	private void blockUser(String uName)
	{
		printMessage(Constants.LOGIN_FATAL_ERROR);
		
		//Add username/ip combination to list of blocked users
		String ipAddr = socket.getInetAddress().getHostAddress();
		List<String> ip = blockedConnections.get(uName);
		if (ip != null)
			ip.add(ipAddr);
		else {
			List<String> l = new ArrayList<String>();
			l.add(ipAddr);
			blockedConnections.put(uName, l);
		}
		
		//Initiate a cleanup task to unblock the user after BLOCK_TIME
		BlockedConnectionCleanupTask cleanupTask = new BlockedConnectionCleanupTask(
				uName, ipAddr);
		Timer timer = new Timer();
		timer.schedule(cleanupTask, Constants.BLOCK_TIME);
	}
	
	//Read offline messages.
	private void readOfflineMessages()
	{
		//Functionality to read stored messages begins here. This code needs to be tested.
		ChatUser user = connections.get(userName);
		
		if(user != null && user.getOfflineMessages() != null && !user.getOfflineMessages().isEmpty())
		{
			printMessage(Constants.UNREAD_MESSAGES);
			for(OfflineMessage msg : user.getOfflineMessages())
			{
				printMessage(msg.getSender().getUserName() + Constants.SEPERATOR + msg.getMessage());
			}
		}
	}
	
	//Print help i.e. commands and usage
	private void handleHelp()
	{
		List<String> commandsToDisplay = new ArrayList<String>();
		Command[] commands = Command.values();
		for(int i=0; i< commands.length; i++)
		{
			if(!commands[i].getDescription().isEmpty())
			{
				commandsToDisplay.add(commands[i].toString());
			}
		}
		printContinuingMessage(commandsToDisplay);
	}
	
	//Handle logout
	private void handleLogout()
	{
		ChatUser user = connections.get(userName);
		if(user != null)
		{
			user.updateLastLoginTime();
			user.setState(State.loggedOut);
		}
	}
	
	//Prints other users logged in
	private void handleWhoElse()
	{
		List<String> users = new ArrayList<String>();
		boolean others = false;
		
		for (ChatUser u : connections.values()) {
			if (!u.getUserName().equals(userName) && u.getState().equals(State.loggedIn))
			{
				users.add(u.getUserName());
				others = true;
			}
		}
		if(!others)
			printMessage(Constants.NO_USER_OL);
		else
			printContinuingMessage(users);
	}
	
	//Prints users online in the last hour
	private void handleWhoLastHr()
	{
		boolean found = false;
		List<String> users = new ArrayList<String>();
		for (ChatUser user : connections.values())
		{
			if(!user.getUserName().equals(userName))
			{
				long lastLoginTime = user.getLastLoginTime();
				if((System.currentTimeMillis() - lastLoginTime) <= Constants.LAST_HOUR ||
							user.getState().equals(State.loggedIn))
				{
					found = true;
					users.add(user.getUserName());
				}
			}
		}
		if(!found)
			printMessage(Constants.NO_USER_LASTHR);
		else
			printContinuingMessage(users);
	}
	
	//Broadcast message to all other users (not to blocked users)
	private void handleBroadCast(String msg)
	{
		if(msg == null || msg.trim().isEmpty())
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
		for (ChatUser u : connections.values()) {
			if (!u.getUserName().equals(userName)) {
				if(u.getBlockedUsers().contains(userName))
				{
					printMessage(String.format(Constants.BLOCKED, u.getUserName()));
				}
				else
				{
					if(u.getState().equals(State.loggedIn))
					{
						PrintWriter writer = u.getWriter();
						printUserMessageToOtherClient(writer, msg);
					}
					else
					{
						OfflineMessage offlineMsg = new OfflineMessage(
								connections.get(userName), msg);
						u.getOfflineMessages().add(offlineMsg);
					}
				}
			}
		}
		printMessage(Constants.MSG_DELIVERED);
	}
	
	//Send private messages to any other users, not to blocked users or users who are in the do not disturb state
	private void handleMessage(String userToMsg, String msgToSend)
	{
		if(userToMsg == null || userToMsg.trim().isEmpty() || msgToSend == null || msgToSend.trim().isEmpty())
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
		user = connections.get(userToMsg);
		if (user != null) {
			if(user.getBlockedUsers().contains(userName.toUpperCase()))
			{
				printMessage(String.format(Constants.BLOCKED, user.getUserName()));
			}
			else
			{
				if(user.getState().equals(State.loggedIn))
				{
					PrintWriter writer = user.getWriter();
					if (!userName.equals(userToMsg) && 
							user.getPresence().equals(PresenceStatus.DONOTDISTURB)){
						printMessage(String.format(Constants.USER_IN_DND, userToMsg));
					}
					else
					{
						printUserMessageToOtherClient(writer, msgToSend);
						printMessage(Constants.MSG_DELIVERED);
					}				
				} else {
					OfflineMessage offlineMsg = new OfflineMessage(connections.get(userName), msgToSend);
					user.getOfflineMessages().add(offlineMsg);
					printMessage(Constants.MSG_STORED);
				}
			}
		}	else {
			printMessage(Constants.USER_OFFLINE);
		}
	}
	
	//Block a user
	private void handleBlockUser(String userToBlock)
	{
		if(userToBlock == null || userToBlock.trim().isEmpty())
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
		if(userToBlock != null)
		{
			user = connections.get(userName);
			user.getBlockedUsers().add(userToBlock.toUpperCase());
		}
		printMessage(String.format(Constants.USER_BLOCKED, userToBlock));
	}
	
	//Unblock a user
	private void handleUnblockUser(String userToUnblock)
	{
		if(userToUnblock == null || userToUnblock.trim().isEmpty())
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
		if(userToUnblock != null)
		{
			user = connections.get(userName);
			user.getBlockedUsers().remove(userToUnblock.toUpperCase());
		}
		printMessage(String.format(Constants.USER_UNBLOCKED, userToUnblock));
	}
	
	//Change the presence of a user, and notify others who have tagged it
	private void handlePresenceChange(String status)
	{
		if(status == null || status.trim().isEmpty())
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
		String statusMessage;
		if(status != null)
		{
			PresenceStatus presenceStatus = getStatus(status);
			user = connections.get(userName);
			user.setPresence(presenceStatus);
			statusMessage = String.format(Constants.PRESENCE_CHANGE, userName, presenceStatus.toString());
			for(String u : user.getTaggedBy())
			{
				if(!user.getBlockedUsers().contains(u.toUpperCase()))
				{
					user = connections.get(u);
					if(user != null)
					{
						PrintWriter out = user.getWriter();
						printUserMessageToOtherClient(out, String.format(statusMessage, userName));
					}
				}
			}
			printMessage(String.format(Constants.PRESENCE_STATUS_CHANGE, presenceStatus.toString()));
		}
	}
	
	//Tag a user for presence updates
	private void handleTagUser(String userToTag)
	{
		if(userToTag != null && !userToTag.isEmpty())
		{
			ChatUser tagUser = connections.get(userToTag);
			if(tagUser != null)
			{
				if(!tagUser.getBlockedUsers().contains(userName.toUpperCase()))
				{
					tagUser.getTaggedBy().add(userName);
					printMessage(String.format(Constants.USER_TAGGED, userToTag));
				}
				else
				{
					printMessage(String.format(Constants.CANNOT_BE_TAGGED, userToTag));
				}
			}
		}
		else
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
	}
	
	//Untag a previously tagged user
	private void handleUntagUser(String userToUntag)
	{
		if(userToUntag != null && !userToUntag.isEmpty())
		{
			ChatUser untagUser = connections.get(userToUntag);
			if(untagUser != null)
			{
				untagUser.getTaggedBy().remove(userName);
				printMessage(String.format(Constants.USER_UNTAGGED, userToUntag));
			}
		}
		else
		{
			printMessage(Constants.INCORRECT_COMMAND);
			return;
		}
	}
	
	private void handleUnknownCommand(String msg)
	{
		printMessage(String.format(Constants.INVALID_COMMAND, msg));
	}
	
	private PresenceStatus getStatus(String status)
	{
		PresenceStatus presenceStatus = null;
		try
		{
			presenceStatus = PresenceStatus.valueOf(status.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(String.format(Constants.INVALID_STATUS, status));
			presenceStatus = PresenceStatus.UNKNOWN;
		}
		return presenceStatus;
	}
	
	private void getInput(String msg)
	{
		out.println(msg);
	}

	private void printMessage(String msg) {
		out.println(Constants.NO_PREFIX_PROMPT + msg + Constants.NEWLINE_INDICATOR);
	}
	
	private void printContinuingMessage(List<String> msg)
	{
		for(int i=0; i<msg.size()-1; i++)
		{
				out.println(Constants.NO_PROMPT + msg.get(i) + Constants.NEWLINE_INDICATOR);
		}	
		printMessage(msg.get(msg.size()-1));
	}

	private void printMessageToOtherClient(PrintWriter out, String msg) {
		out.println(msg + Constants.NEWLINE_INDICATOR);
	}
	
	private void printUserMessageToOtherClient(PrintWriter out, String msg) {
		out.println(Constants.NO_PREFIX_PROMPT + userName + Constants.SEPERATOR + msg + Constants.NEWLINE_INDICATOR);
	}

	private void printNextPrompt() {
		out.println(Constants.NEWLINE_INDICATOR + Constants.PROMPT);
		out.flush();
	}

	//Server is shutting down.Perform cleanup
	public static synchronized void stop()
	{
		isStopped = true;
		closeClientConnections();
	}
	
	//Close existing connections.
	public static synchronized void closeClientConnections() {
		for (ChatUser user : connections.values()) {
			try {
				PrintWriter writer = user.getWriter();
				writer.println(Constants.SERVER_OFFLINE);
				writer.flush();	 
				user.getSocket().close();
			} catch (Exception e) {
				System.err.println("Error occurred: " + e.getMessage());
				e.printStackTrace();
			}
		}
		connections.clear();
		connections = null;
	}

	//Remove a connection from the blocked list
	public static synchronized void cleanup(String userName, String ipAddress) {
		isStopped = true;
		List<String> ips = blockedConnections.get(userName);
		if (ips != null) {
			for (String ip : ips) {
				if (ip.equals(ipAddress)) {
					ips.remove(ip);
					if (ips.isEmpty()) {
						blockedConnections.remove(userName);
					}
					break;
				}
			}
		}
	}
}
