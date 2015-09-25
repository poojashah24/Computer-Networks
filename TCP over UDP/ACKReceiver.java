import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ACKReceiver extends Thread {
	private ServerSocket serverSocket;
	private Socket socket;
	
	private LogManager logManager;
	
	private TCPSegment segment;
	private TCPHeader header;
	private int ackNumber;
	
	private boolean ackReceived;
	private boolean finReceived;
	private boolean done;
	
	
	private byte[] buf;
	
	public ACKReceiver(int ackPort, LogManager logManager) throws SocketException{
		
		try
		{
			this.logManager = logManager;
			serverSocket = new ServerSocket(ackPort);
			done = false;
			ackReceived = false;
		}
		catch (IOException e)
		{
			String errmsg = Constants.SOCKET_EXCEPTION + Constants.SEPERATOR + ackPort;
			logManager.logMessage(errmsg);
			throw new SocketException(errmsg);
		}
	}
	
	public void setDone()
	{
		done = true;
	}
	
	public boolean ackReceived()
	{
		return ackReceived;
	}
	
	public void resetAckReceived()
	{
		synchronized (this) {
			ackReceived = false;
		}	
	}
	
	public int ackNumberReceived()
	{
		return ackNumber;
	}
	
	
	public boolean finReceived()
	{
		return finReceived;
	}
	
	@Override
	public void run() {
		try
		{	
			socket = serverSocket.accept();
			while(!done)
			{
				
					buf = new byte[Constants.ACK_SIZE];

					DataInputStream ip = new DataInputStream(socket.getInputStream());
					ip.read(buf);
					
					segment = (TCPSegment) Serializer.deserialize(buf);
					header = segment.getHeader();
					if(header.isACK())
					{
						if(header.isFIN())
							finReceived = true;
						
						ackNumber = header.getAckNumber();			
						logManager.log(Constants.RECEIVED, socket.getInetAddress().getHostAddress()  + Constants.SEPERATOR + header.getSourcePortNumber(), InetAddress.getLocalHost().getHostAddress() + Constants.SEPERATOR + header.getDestinationPortNumber(), header.getSequenceNumber(),
								header.getAckNumber(), header.isACK(), header.isFIN());
						if(ackNumber >= TCPSender.expectedSeqNum)
						{
							synchronized (this) {
								ackReceived = true;
								notify();
							}
						}
					}
				}
		}
		catch(IOException e)
		{
			System.err.println(Constants.ACK_LISTEN_ERROR);
			logManager.logMessage(Constants.ACK_LISTEN_ERROR + Constants.SEPERATOR + e.getMessage());
		}
	}
}
