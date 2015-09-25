import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TCPSender {
	
	/**
	 * This will be invoked by the upper layer data provider to send chunks of data
	 * @author Pooja
	 */
	
	private String remoteIP;
	private short remotePort;
	private short ackPort;
	
	private ACKReceiver ackReceiver;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private TCPHeader header;
	
	private long sendTime;
	private long receiveTime;
	private double sampleRTT;
	private int seqNum;
	
	static int expectedSeqNum;
	
	private double timeout;
	private int retransmissionCount;
	private int byteCount;
	private int segmentCount;
	
	private LogManager logManager;
	
	public TCPSender(String remoteIP, short remotePort, short ackPort, LogManager logManager) throws SocketException{

		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.ackPort = ackPort;
		this.seqNum = 0;
		this.expectedSeqNum = Constants.MAXIMUM_SEGMENT_SIZE;
		this.retransmissionCount = 0;
		this.timeout = 100;
		
		this.logManager = logManager;
		
		this.socket = new DatagramSocket(ackPort);
		socket.connect(new InetSocketAddress(remoteIP, remotePort));
		
		ackReceiver = new ACKReceiver(ackPort, logManager);
		ackReceiver.start();
	}
	
	/**
	 * BLOCKING call that returns only when the sender is ready to accept the next data
	 * @author Pooja @param data
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void sendData(byte[] data) throws IOException
	{
		
		if(data != null)
			byteCount += Utils.trimBuffer(data).length;
				
		header = new TCPHeader(ackPort, remotePort);
		header.setSequenceNumber(seqNum);
		header.setInternetChecksum(new byte[2]);
		
		TCPSegment segment = new TCPSegment(header);
		if(data == null)
		{
			header.setFin();
		}
		else
			segment.setData(data);
		
		segment = ChecksumHelper.computeAndAddChecksum(segment);
	    byte[] buf = Serializer.serialize(segment);
		packet = new DatagramPacket(buf, buf.length);
				
		sendPacket();
	}
	
	private void sendPacket() throws IOException
	{
		segmentCount += 1;
		socket.send(packet);
		
		logManager.log(Constants.SENT, InetAddress.getLocalHost().getHostAddress() + Constants.SEPERATOR + ackPort, remoteIP + Constants.SEPERATOR + remotePort, header.getSequenceNumber(), header.getAckNumber(), header.isACK(), header.isFIN(), timeout);
		sendTime = System.currentTimeMillis();
		waitForACK();
		
	}
	
	private void waitForACK() throws IOException
	{
		synchronized (ackReceiver) {
			if(!ackReceiver.ackReceived())
			{
					try{
						ackReceiver.wait(new Double(timeout).longValue());
						if(ackReceiver.ackReceived())
						{
							if(header.isFIN() && ackReceiver.finReceived())
							{
								//Shutdown
								logManager.shutDown();
								
								System.out.println(Constants.SUCCESS);
								System.out.println(Constants.TOTAL_BYTES + byteCount);
								System.out.println(Constants.TOTAL_SEGMENT + segmentCount);
								System.out.println(Constants.RETRANSMISSION + retransmissionCount);
								System.out.println();
								
								ackReceiver.setDone();
								return;
							}
							int ackNum = ackReceiver.ackNumberReceived();
							if(ackNum > seqNum)
							{
								receiveTime = System.currentTimeMillis();
								sampleRTT = receiveTime - sendTime;
								timeout = RTTEstimator.getTimeout(sampleRTT);
								seqNum = ackNum;
								expectedSeqNum += Constants.MAXIMUM_SEGMENT_SIZE;
							}
						}
						else
						{
							logManager.logMessage(Constants.TIMEOUT + (seqNum));
							retransmissionCount++;
							sendPacket();
						}
					}
					catch(InterruptedException ie)
					{
						logManager.logMessage(Constants.INTERRUPTED_EXCEPTION);
					}				
				}
			ackReceiver.resetAckReceived();	
		}	
	}
}
