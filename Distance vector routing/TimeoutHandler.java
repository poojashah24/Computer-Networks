import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

public class TimeoutHandler implements Runnable {

	private Neighbor neighbor;

	public TimeoutHandler(Neighbor n) {
		this.neighbor = n;
	}

	public void run()
	{
		//The neighbor didn't send a distance vector until timeout. Mark this link as down.
		synchronized (neighbor.getLock()) {
			
			String key = IPUtils.getUniqueKey(neighbor.getIpAddress(), neighbor.getPort());
			Neighbor n = DVState.getNeighbors().get(key);
			n.setLinkDown();
			
			DVEntry entry = DVState.getDistanceVector().get(key);
			entry.setCost(Double.POSITIVE_INFINITY);

			//Re-compute the distance vector
			boolean dvChanged = DVState.computeDistanceVector(neighbor);
			
			//If the distance vector changes, send an update to all
			if(dvChanged)
			{
				//System.out.println("Sending distance vector to all");
				try {
					DVSenderUtil.sendVectorToAllNeighbors();
				} catch (IOException e) {
					System.err.println(Constants.SEND_ERROR);
				}
			}
		}
	}
}
