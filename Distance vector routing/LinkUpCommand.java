import java.io.IOException;


public class LinkUpCommand extends Command{
	private String ipAddress;
	private int port;
	private boolean received;
	
	public LinkUpCommand(String ipAddress, int port, boolean received)
	{
		commandEnum = CommandEnum.LINKUP;
		this.ipAddress = ipAddress;
		this.port = port;
		this.received = received;
	}
	
	public String getIPAddress()
	{
		return ipAddress;
	}
	
	public int getPort()
	{
		return port;
	}

	@Override
	public void execute() throws IOException {		
		boolean dvChanged = true;
		String key = IPUtils.getUniqueKey(senderIP, senderPort);

		Neighbor neighbor = DVState.getNeighbors().get(key);
		
		if(neighbor == null)
		{
			System.err.println(Constants.NO_NEIGHBOUR);
			return;
		}
		
		if(neighbor != null)
		{
			synchronized (neighbor.getLock()) {
				neighbor.setLinkUp();
				DVState.addDVTask(neighbor);
				DVState.addTimeoutTask(neighbor);
			}
		}
		
		//Update my distance vector for this node
		DVEntry entry = DVState.getDistanceVector().get(key);
		entry.setCost(neighbor.getCost());
		entry.setNeighbor(neighbor);
		
		if(!received)
			DVSenderUtil.sendLinkUpMessage(neighbor);
		
		//Re-calculate the distance vector
		dvChanged = DVState.computeDistanceVector(neighbor);
		
		//If distance vector changes, send an update to all
		if(dvChanged)
		{
			DVSenderUtil.sendVectorToAllNeighbors();
		}
	}
}
