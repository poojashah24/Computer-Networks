import java.io.IOException;


public class DVSender implements Runnable {

	private Neighbor neighbor;
	
	public DVSender(Neighbor n) {
		this.neighbor = n;
	}
	
	@Override
	public void run() {
		try {
			
			DVSenderUtil.sendVectorToNeighbor(neighbor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException re)
		{
			re.printStackTrace();
		}
	}

}
