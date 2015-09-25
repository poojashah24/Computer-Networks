import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * This class represents a chat user
 * @author Pooja
 *
 */
public class ChatUser {
	
	private final String userName;
	private final Socket socket;
	private final BufferedReader reader;
	private final PrintWriter writer;
	private long lastLoginTime;
	private State state;
	private List<OfflineMessage> offlineMessages;
	private List<String> blockedUsers;
	private PresenceStatus presence;
	private List<String> taggedBy;
	
	public ChatUser(String name, Socket sock, BufferedReader reader, PrintWriter writer) {
		this.userName = name;
		this.socket = sock;
		this.reader = reader;
		this.writer = writer;
		this.lastLoginTime = System.currentTimeMillis();
		this.state = State.loggedIn;
		this.offlineMessages = new ArrayList<OfflineMessage>(1);
		this.blockedUsers = new ArrayList<String>(1);
		this.presence = PresenceStatus.AVAILABLE;
		this.taggedBy = new ArrayList<String>(1);
	}
	
	public ChatUser(String name, Socket sock, BufferedReader reader,
			PrintWriter writer, List<OfflineMessage> offlineMessages) {
		this(name, sock, reader, writer);
		this.offlineMessages = offlineMessages;
	}

	public String getUserName() {
		return userName;
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	
	public void updateLastLoginTime()
	{
		lastLoginTime = System.currentTimeMillis();
	}

	public void setState(State s)
	{
		this.state = s;
	}
	
	public State getState() {
		return state;
	}

	public List<OfflineMessage> getOfflineMessages() {
		return offlineMessages;
	}

	public List<String> getBlockedUsers() {
		return blockedUsers;
	}

	public void setPresence(PresenceStatus status) {
		this.presence = status;
	}
	
	public PresenceStatus getPresence() {
		return presence;
	}

	public List<String> getTaggedBy() {
		return taggedBy;
	}
	
}
