import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class Neighbor {
	private final String ipAddress;
	private final int port;
	private double cost;
	private LinkState state;
	private boolean neighbor;	
	private DatagramSocket sendSocket;
	
	private List<DVEntry> distanceVector;
	
	private Object lock;
	
	public Neighbor(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.state = LinkState.UP;
		lock = new Object();
		distanceVector = new ArrayList<DVEntry>();
	}
	
	public Neighbor(String ipAddress, int port, double cost) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.cost = cost;
		this.state = LinkState.UP;
		this.neighbor = true;
		this.sendSocket = null;
		lock = new Object();
		distanceVector = new ArrayList<DVEntry>();
	}
	
	public void start() throws IOException
	{
		sendSocket = new DatagramSocket();
		sendSocket.connect(new InetSocketAddress(ipAddress, port));
		/*try
		{
			Thread.sleep(1000);
			sendSocket = new DatagramSocket();
			sendSocket.connect(new InetSocketAddress(ipAddress, port));
		}
		catch(InterruptedException ie)
		{
			System.err.println("Caught interrupted exception while starting");
		}*/
		
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean isUP() {
		return state.equals(LinkState.UP);
	}

	public void setLinkDown()
	{
		this.state = LinkState.DOWN;
	}
	
	public void setLinkUp()
	{
		this.state = LinkState.UP;
	}

	public boolean isNeigbor()
	{
		return neighbor;
	}

	public void setNeigbor(boolean neighbor)
	{
		this.neighbor = neighbor;
	}
	
	public void updateDistanceVector(List<DVEntry> dv)
	{
		this.distanceVector = dv;
	}
	
	public List<DVEntry> getDistanceVector()
	{
		return distanceVector;
	}
	
	public DatagramSocket getSocket() {
		return sendSocket;
	}
	
	public Object getLock() {
		return lock;
	}

	public boolean equals(Object obj)
	{
		return (obj instanceof Neighbor) ? (((Neighbor)obj).ipAddress.equals(this.ipAddress) &&
				((Neighbor)obj).port == this.port) : false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.DESTINATION);
		builder.append(MessageFormat.format(Constants.ADDRESS_FORMAT, ipAddress, port));
		builder.append(Constants.COST);
		builder.append(String.format(Constants.COST_FORMAT, cost));
		
		return builder.toString();
	}
}
