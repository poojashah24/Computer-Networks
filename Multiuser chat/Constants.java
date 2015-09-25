/**
 * Class for all the constants - string literals, numbers etc.
 * @author Pooja
 *
 */
public class Constants {

	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String SEPERATOR = ":";
	public static final String PROMPT = ">";
	public static final String NEWLINE_INDICATOR = "EOL";
	public static final String NO_PROMPT = "NOP";
	public static final String NO_PREFIX_PROMPT = "NPP";

	public static final String NO_PORT = "Please enter a port number to start the server";
	public static final String SERVER_STARTED = "Server started successfully";
	public static final String LOGIN_PROMPT = "Username:";
	public static final String PASSWORD_PROMPT = "Password:";
	public static final String CREDENTIALS_FILE = "user_pass.txt";
	public static final String WELCOME_PROMPT = "Welcome to simple chat server!";
	public static final String LOGIN_FATAL_ERROR = "You have exceeded the maximum number of login attempts.";
	public static final String ACCESS_DISABLED = "All access for this user and ip address has been disabled. Please try after some time.";
	public static final String INVALID_LOGIN_DETAILS = "Invalid username or password entered";
	public static final String DUPLICATE_LOGIN = "User is already logged in! Cannot login again.";
	public static final String USER_OFFLINE = "The user is not online. Your message cannot be delivered";
	public static final String MSG_STORED = "The user is not online. Your message will be delivered when the user comes online";
	public static final String SERVER_OFFLINE = "The server is going offline. Your session will be terminated";
	public static final String SERVER_GOING_OFFLINE = "Server going offline now.";
	public static final String SESSION_EXPIRED = "Your session has expired. You will now be disconnected";
	public static final String INVALID_COMMAND = "Command %s not recognized. Type help to get list of commands and usage";
	public static final String INCORRECT_COMMAND = "Invalid command parameters. Type help to get list of commands and usage";
	public static final String INVALID_STATUS = "Presence status %s not recognized";
	public static final String UNREAD_MESSAGES = "You have unread messages";
	public static final String USER_BLOCKED = "User %s successfully blocked";
	public static final String USER_UNBLOCKED = "User %s successfully unblocked";
	public static final String BLOCKED = "User %s has blocked you. No messages will be delivered";
	public static final String USER_TAGGED = "User %s successfully tagged";
	public static final String USER_UNTAGGED = "User %s successfully untagged";
	public static final String CANNOT_BE_TAGGED = "User %s has blocked you. You cannot tag the user";
	public static final String PRESENCE_CHANGE = "User %s is now %s";
	public static final String PRESENCE_STATUS_CHANGE = "Presence status changed to %s";
	public static final String USER_IN_DND = "User %s cannot be disturbed";
	public static final String MSG_DELIVERED = "Your message has been delivered";
	public static final String NO_USER_OL = "No other user is online";
	public static final String NO_USER_LASTHR = "No other user was online within the last hour";

	public static final String LOGOUT_DESC = "Logs out this user";
	public static final String WHOELSE_DESC = "Displays name of other connected users";
	public static final String WHOLASTHR_DESC = "Displays name of only those users that connected within the last hours";
	public static final String BROADCAST_DESC = "Broadcasts <message> to all connected users";
	public static final String MSG_DESC = "Private <message> to a <user>";
	public static final String BLOCK_DESC = "Block <user>. <user> will no longer be able to send messages to you/tag you";
	public static final String UNBLOCK_DESC = "Unblock <blockeduser>. Unblocks a previously blocks user";
	public static final String TAG_DESC = "Tag <user>. Get presence updates from the user";
	public static final String UNTAG_DESC = "Untag <user>. Untag a previously tagged user";
	public static final String PRESENCE_DESC = "Presence <presence status>. Set presence to <available>, <busy>, <donotdisturb> or <away>. Note: You will not get messages in donotdisturb status";

	public static final int LOGIN_ATTEMPTS = 3;

	// This value is represented in milliseconds
	public static final int BLOCK_TIME = 60000;

	// This value is represented in milliseconds
	public static final int TIME_OUT = 1800000;

	// This value is represented in milliseconds
	public static final int LAST_HOUR = 3600000;

}
