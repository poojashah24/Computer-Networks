import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.List;


public class DVListener extends Thread {

	private DatagramSocket receiveSocket;
	private boolean done;
	byte[] readBuffer;
	
	public DVListener(int port) throws SocketException {
		try {
			receiveSocket = new DatagramSocket(new InetSocketAddress(IPUtils.getMyIP(), port));
			done = false;
		} catch (IOException ioe) {
			throw new SocketException(MessageFormat.format(Constants.SOCKET_ERROR,
					IPUtils.getMyIP(), String.format("%d", port)) + " " + ioe.getMessage());
		}
	}
	
	public void setDone()
	{
		done = true;
		this.interrupt();
	}
	
	@Override
	public void run() {
		try {
			DatagramPacket packet = null;
			readBuffer = new byte[Constants.BUFFER_SIZE];
			while(!done)	
			{
				packet = new DatagramPacket(readBuffer, Constants.BUFFER_SIZE);
				receiveSocket.receive(packet);

				List<Command> commands = JSONHelper.deserialize(new String(packet.getData(), 
															Constants.ENCODING));
				for(Command command : commands)
				{
					command.execute();
				}
			}
		} catch(Exception e)
		{
			if(!done)
			{
				System.err.println("Error occurred while listening: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
