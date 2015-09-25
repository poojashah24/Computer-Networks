import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


public class DVSenderUtil {
	public static void sendVectorToAllNeighbors() throws IOException {

		String jsonDistanceVector = null;
		ByteBuffer buffer = null;
		DatagramPacket pkt = null;
		
		List<DVEntry> myDV = (List<DVEntry>) new ArrayList<DVEntry>(DVState
				.getDistanceVector().values());
				
		for (Entry<String, Neighbor> neighbor : DVState.getNeighbors().entrySet()) {
			
			//Check if the link is up
			if(neighbor.getValue().isUP())
			{
				/*Adjust the DV for each neighbor to avoid count-to-infinity */
				List<DVEntry> adjustedDV = getAdjustedDVList(myDV, neighbor.getValue());
				jsonDistanceVector = JSONHelper.serialize(
						CommandEnum.ROUTE_UPDATE.toString(), adjustedDV,
						IPUtils.getMyIP(), DVState.getMyPort());
				
				/*Done getting the adjusted DV. Prepare to send */
				buffer = ByteBuffer.wrap(jsonDistanceVector.getBytes());
				pkt = new DatagramPacket(buffer.array(), buffer.array().length);

				pkt.setSocketAddress(new InetSocketAddress(neighbor.getValue()
						.getIpAddress(), neighbor.getValue().getPort()));
				
				neighbor.getValue().getSocket().send(pkt);
				
				//Cancel the DV task and re-queue
				DVState.cancelDVTask(IPUtils.getUniqueKey(neighbor.getValue()
						.getIpAddress(), neighbor.getValue().getPort()));
				DVState.addDVTask(neighbor.getValue());
			}
				
		}
	}
	
	/**
	 * This method should be used when TIMEOUT expires for a node, and it needs to send a
	 * DV to a specific node. Note that this value is different for different nodes, and they
	 * can join at varying times / also in cases of link down / link up
	 * @author Pooja @param n
	 * @author Pooja @throws IOException
	 */
	public static void sendVectorToNeighbor(Neighbor n) throws IOException {
		
		List<DVEntry> myDV = (List<DVEntry>) new ArrayList<DVEntry>(DVState
				.getDistanceVector().values());

		/*Adjust the DV for each neighbor to avoid count-to-infinity */
		List<DVEntry> adjustedDV = getAdjustedDVList(myDV, n);
		String jsonDistanceVector = JSONHelper.serialize(
				CommandEnum.ROUTE_UPDATE.toString(), adjustedDV,
				IPUtils.getMyIP(), DVState.getMyPort());
		
		ByteBuffer buffer = ByteBuffer.wrap(jsonDistanceVector.getBytes());
		for (Entry<String, Neighbor> neighbor : DVState.getNeighbors().entrySet()) {
			if(neighbor.getValue().equals(n))
			{
				DatagramPacket pkt = new DatagramPacket(buffer.array(), buffer.array().length);
				pkt.setSocketAddress(new InetSocketAddress(n.getIpAddress(), n.getPort()));
				neighbor.getValue().getSocket().send(pkt);
				
				//Cancel the DV task and re-queue
				DVState.cancelDVTask(IPUtils.getUniqueKey(neighbor.getValue()
						.getIpAddress(), neighbor.getValue().getPort()));
				DVState.addDVTask(neighbor.getValue());
			}
			
		}
	}
	
	public static void sendLinkDownMessage(Neighbor n)
	{
		String linkDownMessage = JSONHelper.serialize(
				CommandEnum.LINKDOWN.name(), n.getIpAddress(), n.getPort(),
				IPUtils.getMyIP(),
				DVState.getMyPort());
		
		ByteBuffer buffer = ByteBuffer.wrap(linkDownMessage.getBytes());
		DatagramPacket pkt = new DatagramPacket(buffer.array(), buffer.array().length);
		
		try {
			n.getSocket().send(pkt);
		} catch (IOException ioe)
		{
			System.err.println("IOException occurred while sending the link down message: "+n.getIpAddress()
							+":"+n.getPort());
		}
		
	}
	
	public static void sendLinkUpMessage(Neighbor n)
	{
		String linkUpMessage = JSONHelper.serialize(
				CommandEnum.LINKUP.name(), n.getIpAddress(), n.getPort(),
				IPUtils.getMyIP(),
				DVState.getMyPort());
		
		ByteBuffer buffer = ByteBuffer.wrap(linkUpMessage.getBytes());
		DatagramPacket pkt = new DatagramPacket(buffer.array(), buffer.array().length);
		
		try {
			n.getSocket().send(pkt);
		} catch (IOException ioe)
		{
			System.err.println("IOException occurred while sending the link down message: "+n.getIpAddress()
							+":"+n.getPort());
		}
	}
	
	private static List<DVEntry> getAdjustedDVList(List<DVEntry> dvEntries, Neighbor n)
	{
		List<DVEntry> newDVEntries = new ArrayList<DVEntry>();
		for(DVEntry e : dvEntries)
		{
			DVEntry newEntry = new DVEntry(e);
			if(e.getNeighbor() != null && e.getNeighbor().equals(n))
			{
				if(!e.getIpAddress().equals(n.getIpAddress()) || e.getPort() != n.getPort())
					newEntry.setCost(Double.POSITIVE_INFINITY);
			}
			newDVEntries.add(newEntry);
		}
		return newDVEntries;
	}	
}
