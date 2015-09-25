import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DVState {
	private final Map<String, DVEntry> distanceVector;
	private final Map<String, Neighbor> neighbors;

	private final ScheduledThreadPoolExecutor dvThreadPool;
	private final Map<String, ScheduledFuture> dvTaskList;

	private final ScheduledThreadPoolExecutor timeoutThreadPool;
	private final Map<String, ScheduledFuture> timeoutTaskList;

	private static int port;
	private static int timeout;
	private static DVListener dvListener;

	private static DVState instance;

	static {
		instance = new DVState();
	}

	private DVState() {
		distanceVector = new HashMap<String, DVEntry>();
		neighbors = new HashMap<String, Neighbor>();

		dvThreadPool = new ScheduledThreadPoolExecutor(20);
		dvTaskList = new HashMap<String, ScheduledFuture>();

		timeoutThreadPool = new ScheduledThreadPoolExecutor(20);
		timeoutTaskList = new HashMap<String, ScheduledFuture>();
	}

	public static synchronized DVState getDVState() {
		if (instance == null)
			instance = new DVState();
		return instance;
	}

	
	public static void setMyPort(int myPort)
	{
		//Ensures this is set only once
		if(port <= 0)
			port = myPort;
	}
	
	public static int getMyPort() {
		return port;
	}
	
	
	public static void setListener(DVListener listener)
	{
		dvListener = listener;
		
	}
	
	public static DVListener getListener() {
		return dvListener;
	}
	
	public static void setTimeout(int timer)
	{
		//Ensures this is set only once
		if(timeout <= 0)
			timeout = timer;
	}
	
	public static int getTimeout() {
		return timeout;
	}

	public static Map<String, DVEntry> getDistanceVector() {
		return instance.distanceVector;
	}

	public static Map<String, Neighbor> getNeighbors() {
		return instance.neighbors;
	}

	public static synchronized void addNeighbor(String key, Neighbor n) {
		instance.neighbors.put(key, n);
	}

	public static synchronized void addDVEntry(String key, DVEntry value) {
		instance.distanceVector.put(key, value);
	}

	public static synchronized void cancelDVTask(String ipAddrPortID) {
		ScheduledFuture future = instance.dvTaskList.get(ipAddrPortID);
		if (future != null && !future.isDone() && !future.isCancelled())
			future.cancel(false);
	}

	public static synchronized void cancelTimeoutTask(String ipAddrPortID) {
		ScheduledFuture future = instance.timeoutTaskList.get(ipAddrPortID);
		if (future != null && !future.isDone() && !future.isCancelled())
			future.cancel(false);
	}

	public static synchronized void addDVTask(Neighbor neighbor) {
		DVSender dvSender = new DVSender(neighbor);
		String key = IPUtils.getUniqueKey(neighbor.getIpAddress(), neighbor.getPort());

		instance.dvTaskList.put(key, instance.dvThreadPool.schedule(dvSender, timeout,
				TimeUnit.SECONDS));
	}

	public static synchronized void addTimeoutTask(Neighbor neighbor) {
		TimeoutHandler timeoutHandler = new TimeoutHandler(neighbor);
		String key = IPUtils.getUniqueKey(neighbor.getIpAddress(), neighbor.getPort());
		
		instance.timeoutTaskList.put(key, instance.timeoutThreadPool.schedule(
				timeoutHandler, timeout * 3, TimeUnit.SECONDS));
	}
	
	public static void waitForCompletion()
	{
		for(ScheduledFuture f : instance.dvTaskList.values())
		{
			f.cancel(false);
		}
		for(ScheduledFuture f : instance.timeoutTaskList.values())
		{
			f.cancel(false);
		}
	}

	public static boolean computeDistanceVector(Neighbor n) {
		/*
		 * In the distributed, asynchronous algorithm, from time to time, each
		 * node sends a copy of its distance vector to each of its neighbors.
		 * When a node x receives a new distance vector from any of its
		 * neighbors v, it saves v’s distance vector, and then uses the
		 * Bellman-Ford equation to update its own distance vector as fol- lows:
		 * Dx(y) = minv{c(x,v) + Dv(y)} for each node y in N If node x’s
		 * distance vector has changed as a result of this update step, node x
		 * will then send its updated distance vector to each of its neighbors,
		 * which can in turn update their own distance vectors.
		 */

		double myCostToNeighbour = -1;
		DVEntry myEntry = null;
		boolean dvChanged = false;
		List<DVEntry> nDistanceVector = n.getDistanceVector();

		//Get my cost from the neighbor. If the neighbor link is down, make it INFINITY.
		Map<String, DVEntry> distanceVector = DVState.getDistanceVector();
				
		String key = MessageFormat.format(
				Constants.ADDRESS_FORMAT, n.getIpAddress(), n.getPort());
		DVEntry nEntry = distanceVector.get(MessageFormat.format(
				Constants.ADDRESS_FORMAT, n.getIpAddress(), n.getPort()));
		
		if(nEntry == null)
			System.out.println("neighbor not found");
		else if (nEntry != null && n.isUP())
			myCostToNeighbour = nEntry.getCost();
		else if (!n.isUP())
			myCostToNeighbour = Double.POSITIVE_INFINITY;
		
		//If there are new nodes added to the network, add them to my DV vector
		for (DVEntry entry : nDistanceVector) {
			myEntry = distanceVector.get(MessageFormat.format(
					Constants.ADDRESS_FORMAT, entry.getIpAddress(),
					entry.getPort()));
			
			if (myEntry == null) {
				// This means that it is a new node added to the network
				// Add it in my DV list
				myEntry = new DVEntry(entry.getIpAddress(), entry.getPort(),
						Double.POSITIVE_INFINITY, n);
				distanceVector.put(
						MessageFormat.format(Constants.ADDRESS_FORMAT,
								entry.getIpAddress(), entry.getPort()), myEntry);
			}
		}
		
		List<DVEntry> neighborsDV = n.getDistanceVector();
		//This is what I added just now
		for(DVEntry dv : distanceVector.values())
		{
			if(dv.getNeighbor() != null && dv.getNeighbor().equals(n))
			{
				for(DVEntry d : neighborsDV)
				{
					if(d.getIpAddress().equals(dv.getIpAddress()) && d.getPort() == dv.getPort())
					{
						dv.setCost(myCostToNeighbour + d.getCost());
						break;
					}
				}
			}
		}
		
		//Dx(y) = minv{c(x,v) + Dv(y)} for each node y in x's distance vector
		for(DVEntry entry : distanceVector.values())
		{			
			//This for loop wasn't there
			for(Neighbor n1 : instance.neighbors.values())
			{
				if(n1.isUP())
				{
					DVEntry nDVEntry = distanceVector.get(IPUtils.getUniqueKey(n1.getIpAddress(), n1.getPort()));
					//myCostToNeighbour = nDVEntry.getCost();
					myCostToNeighbour = n1.getCost();
					/*System.out.println("neighbor:" + n1.getIpAddress() + n1.getPort() + myCostToNeighbour);
					System.out.println("dventry:" + nDVEntry);*/

					for(DVEntry nDV : n1.getDistanceVector())
					{
						if(nDV.getIpAddress().equals(entry.getIpAddress()) &&
								nDV.getPort() == entry.getPort())
						{
							double nCostToNode = nDV.getCost();
							
							if(entry.getCost() > myCostToNeighbour + nCostToNode)
							{
								entry.setCost(myCostToNeighbour + nCostToNode);
								entry.setNeighbor(n1);
								dvChanged = true;
							}
						}
					}
				}	
			}
		}
		return dvChanged;
	}

	public static void printRoutingTable() {
		
		
		Date date = new Date(System.currentTimeMillis());
		System.out.print(date);
		System.out.println(Constants.DVLIST);
		for (DVEntry dvEntry : instance.distanceVector.values()) {
			if(dvEntry.getIpAddress().equals(IPUtils.getMyIP()) && dvEntry.getPort() == port)
				continue;
			System.out.println(dvEntry.toString());
		}
	}
}
