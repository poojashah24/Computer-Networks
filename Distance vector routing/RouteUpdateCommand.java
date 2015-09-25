import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class RouteUpdateCommand extends Command {

	List<DVEntry> distanceVector = new ArrayList<DVEntry>();
	
	public RouteUpdateCommand(List<DVEntry> distanceVector)
	{
		commandEnum = CommandEnum.ROUTE_UPDATE;
		this.distanceVector = distanceVector;
	}
	
	public List<DVEntry> getDistanceVectors()
	{
		return distanceVector;
	}

	@Override
	public void execute() throws IOException {		
		String key = IPUtils.getUniqueKey(senderIP, senderPort);
		Neighbor neighbor = DVState.getNeighbors().get(key);
		
		if(neighbor == null)
		{
			//This means that this is a newly added node.
			//Add it to the list of neighbors
			//System.out.println("got a new neighbor");
			neighbor = new Neighbor(senderIP, senderPort);
			for(DVEntry entry : distanceVector)
			{
				if(entry.getIpAddress().equals(IPUtils.getMyIP()) && entry.getPort() == DVState.getMyPort())
				{
					neighbor.setCost(entry.getCost());					
				}
			}
			neighbor.start();
			DVState.addNeighbor(key, neighbor);
			DVEntry dvEntry = new DVEntry(neighbor.getIpAddress(), neighbor.getPort(),
					neighbor.getCost(), neighbor);
			DVState.addDVEntry(MessageFormat.format(Constants.ADDRESS_FORMAT,
					neighbor.getIpAddress(), neighbor.getPort()), dvEntry);
			DVState.addDVTask(neighbor);
		}

		//Update the distance vector sent by the neighbor
		if(!neighbor.isUP())
		{
			neighbor.setLinkUp();
			for (DVEntry entry : DVState.getDistanceVector().values()) {
				if (entry.getIpAddress().equals(neighbor.getIpAddress())
						&& entry.getPort() == neighbor.getPort()) {
					entry.setCost(neighbor.getCost());
					break;
				}
			}
			
		}
		neighbor.updateDistanceVector(distanceVector);
		
		//If there is a timeout task, cancel it. Restart the timer.
		DVState.cancelTimeoutTask(key);
		DVState.addTimeoutTask(neighbor);
		
		//Recompute the distance vector
		boolean changed = DVState.computeDistanceVector(neighbor);
		//If the distance vector has changed, re-send it to all
		if(changed)
		{
			DVSenderUtil.sendVectorToAllNeighbors();
		}
	}
}
