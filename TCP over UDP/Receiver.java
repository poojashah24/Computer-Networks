import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

public class Receiver {
	
	private String fileName;
	private String logFileName;
	private String senderIP;
	private short senderPort;
	private short listeningPort;
	private LogManager logManager;
	
	private DatagramSocket listeningSocket;
	private DatagramPacket packet;
	
	private int expectedSeqNum;
	
	private Socket senderSocket;
	
	private PrintWriter writer;
	private DataOutputStream socketWriter;
	private boolean done;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if(args == null || args.length < 5)
		{
			System.err.println(Constants.INVALID_PARAMETERS);
			System.err.println(Constants.USAGE);
			return;
		}
		Receiver receiver = new Receiver();
		boolean validationResult = receiver.validateInput(args);
		if(!validationResult)
		{
			return;
		}
		receiver.initialize();
		while(!receiver.done)
		{
			receiver.receive();
		}
	}
	
	private void initialize()
	{
		try
		{
			logManager = new LogManager(logFileName);
			listeningSocket = new DatagramSocket(new InetSocketAddress(listeningPort));
			File f = new File(fileName);
			if(f.exists())
			{
				f.delete();
				f.createNewFile();
			}
			writer = new PrintWriter(fileName);
			expectedSeqNum = 0;
			done = false;
		}
		catch(SocketException se)
		{
			logManager.logMessage(Constants.SOCKET_EXCEPTION + Constants.SEPERATOR + listeningPort);
		}
		catch(IOException ioe)
		{
			System.err.println(Constants.FILE_CREATION_FAILED);
			logManager.logMessage(Constants.FILE_CREATION_FAILED);
		}
	}
	
	
	private void receive() throws IOException, ClassNotFoundException
	{
		byte[] buf = new byte[Constants.RECEIVER_BUFFER_SIZE];
		packet = new DatagramPacket(buf, buf.length);
		listeningSocket.receive(packet);
		
		if(senderSocket == null)
		{
			senderSocket = new Socket(senderIP, senderPort);
			socketWriter = new DataOutputStream(senderSocket.getOutputStream());
		}
			
		buf = packet.getData();
		TCPSegment segment = null;
		TCPHeader header = null;
		try
		{
			segment = (TCPSegment)Serializer.deserialize(buf);
			header = segment.getHeader();

			logManager.log(Constants.RECEIVED, senderIP + Constants.SEPERATOR + header.getSourcePortNumber(), InetAddress.getLocalHost().getHostAddress() + Constants.SEPERATOR + header.getDestinationPortNumber(), header.getSequenceNumber(),
					header.getAckNumber(), header.isACK(), header.isFIN());
			if(header.isFIN())
			{
				done = true;
				
				//Create new TCP segment for FIN
				header = new TCPHeader(listeningPort, senderPort);
				header.setAck();
				header.setAckNumber(expectedSeqNum + Constants.MAXIMUM_SEGMENT_SIZE);
				header.setFin();
				header.setInternetChecksum(new byte[2]);
				segment = new TCPSegment(header);
				segment.setData(null);
				
				//Send the FIN segment to the sender
				byte[] ackBuf = Serializer.serialize(segment);				
				socketWriter.write(ackBuf);
				socketWriter.flush();
				logManager.log(Constants.SENT, InetAddress.getLocalHost().getHostAddress() + Constants.SEPERATOR + listeningPort, senderIP + Constants.SEPERATOR + senderPort, header.getSequenceNumber(),
						header.getAckNumber(), header.isACK(), header.isFIN());
				
				System.out.println(Constants.SUCCESS);
				writer.close();
			}
			else
			{
				if(ChecksumHelper.validateChecksum(segment) && header.getSequenceNumber() == expectedSeqNum)
				{
					byte[] data = segment.getData();
					writer.write(new String(Utils.trimBuffer(data), Constants.ENCODING));
					expectedSeqNum += Constants.MAXIMUM_SEGMENT_SIZE;
				}

				//Create new TCP segment for ACK
				header = new TCPHeader(listeningPort, senderPort);
				header.setAckNumber(expectedSeqNum);
				header.setAck();
				header.setInternetChecksum(new byte[2]);
				segment = new TCPSegment(header);
				segment.setData(null);
				
				//Send the ACK segment to the sender
				byte[] ackBuf = Serializer.serialize(segment);
				
				socketWriter.write(ackBuf);
				logManager.log(Constants.SENT, InetAddress.getLocalHost().getHostAddress() + Constants.SEPERATOR + listeningPort, senderIP + Constants.SEPERATOR + senderPort, header.getSequenceNumber(),
						header.getAckNumber(), header.isACK(), header.isFIN());
			}
		}
		catch(EOFException eof)
		{
			logManager.logMessage(Constants.ERROR_WRITING_FILE);
		}
	}
	
	private boolean validateInput(String[] args)
	{
		boolean result = true;
		
		fileName = args[0];
		logFileName = args[4];
		senderIP = args[2];
		
		if(!validateIPAddress(senderIP))
		{
			System.err.println(Constants.INVALID_IP_ADDRESS);
			result = false;
		}
		try
		{
			listeningPort = Short.parseShort(args[1]);
			senderPort = Short.parseShort(args[3]);
		}
		catch(NumberFormatException nfe)
		{
			System.err.println(Constants.INVALID_PORT_NUMBER);
			result = false;
		}
		
		return result;
	}
	
	private boolean validateIPAddress(String ipAddress)
	{
		return Pattern.matches(Constants.IPADDRESS_REGEX, ipAddress);
	}
}
