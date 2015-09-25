import java.text.MessageFormat;


public class DVEntry {
	private final String ipAddress;
	private final int port;
	private double cost;
	
	//This field represents the neighbour used to reach to the client
	private Neighbor neighbor;
	
	public DVEntry(String ipAddress, int port, double cost, Neighbor neighbor) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.cost = cost;
		this.neighbor = neighbor;
	}
	
	public DVEntry(DVEntry copy)
	{
		this.ipAddress = copy.ipAddress;
		this.port = copy.port;
		this.cost = copy.cost;
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

	public Neighbor getNeighbor() {
		return neighbor;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setNeighbor(Neighbor neighbor) {
		this.neighbor = neighbor;
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.DESTINATION).append(MessageFormat.format(Constants.ADDRESS_FORMAT, ipAddress, String.format("%d",port)));
		builder.append(Constants.SPACE);
		builder.append(Constants.COST).append(String.format(Constants.COST_FORMAT, cost));
		builder.append(Constants.SPACE);
		if(neighbor != null)
			builder.append(Constants.LINK).append(MessageFormat.format(Constants.LINK_FORMAT, neighbor.getIpAddress(), String.format("%d", neighbor.getPort())));
		else
			builder.append(Constants.LINK).append(MessageFormat.format(Constants.LINK_FORMAT, IPUtils.getMyIP(), String.format("%d", DVState.getMyPort())));
		return builder.toString();
	}
}
