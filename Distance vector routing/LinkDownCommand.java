import java.io.IOException;


public class LinkDownCommand extends Command {
	private String ipAddress;
	private int port;
	private boolean received;
	
	public LinkDownCommand(String ipAddress, int port, boolean received)
	{
		commandEnum = CommandEnum.LINKDOWN;
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
		boolean dvChanged = false;
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
				neighbor.setLinkDown();
				DVState.cancelDVTask(key);
				DVState.cancelTimeoutTask(key);
			}
		}			
		
		DVEntry entry = DVState.getDistanceVector().get(key);
		entry.setCost(Double.POSITIVE_INFINITY);
		
		if(!received)
			DVSenderUtil.sendLinkDownMessage(neighbor);

		//Re-compute the distance vector
		dvChanged = DVState.computeDistanceVector(neighbor);
		
		//If the distance vector changes, send an update to all
		if(dvChanged)
		{
			DVSenderUtil.sendVectorToAllNeighbors();
		}
	}
}
