import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class BFNode {
	
	private final String myIP;
	private final int port;
	private final int timeout;
	private final DVListener listener;
	private final Neighbor me;

	// This is the distance vector maintained by this node for all its
	// neighbors.
	// It contains the neighbor, and the distance vector sent by the neighbor
	private final HashMap<Neighbor, List<DVEntry>> neighborDistanceVectors;

	public BFNode(int port, int timeout) throws IOException {
		myIP = IPUtils.getMyIP();
		this.port = port;
		this.timeout = timeout;
		
		DVState.setMyPort(port);
		DVState.setTimeout(timeout);
		
		listener = new DVListener(port);
		DVState.setListener(listener);
		
		neighborDistanceVectors = new HashMap<Neighbor, List<DVEntry>>();
		me = new Neighbor(myIP, this.port, 0);
		
		DVEntry myEntry = new DVEntry(myIP, port, 0, null);
		DVState.addDVEntry(IPUtils.getUniqueKey(myIP, port), myEntry);
	}

	public void start() throws SocketException {
		
		listener.start();
		
		Neighbor neighbour = null;
		try {
			for (Entry<String, Neighbor> e : DVState.getNeighbors().entrySet()) {
				neighbour = (Neighbor) e.getValue();
				neighbour.start();
				
				DVState.addDVTask(neighbour);
				DVState.addTimeoutTask(neighbour);
			}
		} catch (IOException ioe) {
			throw new SocketException(String.format(Constants.SOCKET_ERROR,
					neighbour.getIpAddress(), neighbour.getPort()));
		}
	}

	public void initializeNeighbours(List<Neighbor> neighbours) {
		for (Neighbor n : neighbours) {
			
			
			//Changed this initialization to the neighbor itself
			DVEntry dvEntry = new DVEntry(n.getIpAddress(), n.getPort(),
					n.getCost(), n);
			DVState.addDVEntry(MessageFormat.format(Constants.ADDRESS_FORMAT,
							n.getIpAddress(), n.getPort()), dvEntry);
			DVState.addNeighbor(MessageFormat.format(Constants.ADDRESS_FORMAT,
					n.getIpAddress(), n.getPort()), n);
			neighborDistanceVectors.put(n, null);
		}
	}
	
	/**
	 * This method should be used when the node's DV has changed, and it wants
	 * to broadcast it to all the neighbors
	 * @author Pooja @throws IOException
	 */
	
	
	private void sendLinkUpMesg(Neighbor neighbor)
	{
		synchronized (neighbor.getLock()) {
			neighbor.setLinkUp();
			String linkUpMessage = JSONHelper.serialize(
					CommandEnum.LINKUP.name(), IPUtils.getMyIP(),
					DVState.getMyPort(), neighbor.getIpAddress(),
					neighbor.getPort());
			
			//Send the LINKUP message to the neighbor as well.
			ByteBuffer buffer = ByteBuffer.wrap(linkUpMessage.getBytes());
			try {
				neighbor.getSocket().send(null);
			} catch (IOException ioe)
			{
				System.err.println("IOException occurred while sending the DV: "+neighbor.getIpAddress()
								+":"+neighbor.getPort());
			}
		}
	}
	
}
