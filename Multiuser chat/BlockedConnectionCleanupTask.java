import java.util.TimerTask;

public class BlockedConnectionCleanupTask extends TimerTask {

	String userName;
	String ipAddress;
	
	public BlockedConnectionCleanupTask(String userName, String ipAddres) {
		this.userName = userName;
		this.ipAddress = ipAddres;
	}
	
	@Override
	public void run() {
		//Remove the username/ip address from the blocked users list.
		ClientConnectionHandler.cleanup(userName, ipAddress);
	}
}
