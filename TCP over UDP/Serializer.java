import java.io.IOException;
import java.nio.ByteBuffer;

public class Serializer {
	
	public static byte[] serialize(TCPSegment segment) throws IOException
	{
		byte[] buf = null;
		byte[] segmentBuf = null;
		if(segment != null)
		{
			TCPHeader header = segment.getHeader();
			if(header != null)
			{	
				ByteBuffer buffer = ByteBuffer.allocate(20);
				buffer.putShort(header.sourcePortNumber);
				buffer.putShort(header.destinationPortNumber);
				buffer.putInt(header.sequenceNumber);
				buffer.putInt(header.ackNumber);
				buffer.putShort(header.offsetAndFlags);
				buffer.putShort(header.receiveWindow);
				buffer.put(header.internetChecksum);
				
				buf = buffer.array();
			}
			if(segment.getData() != null)
			{
				segmentBuf = new byte[buf.length + segment.getData().length];
				System.arraycopy(buf, 0, segmentBuf, 0, buf.length);
				System.arraycopy(segment.getData(), 0, segmentBuf, buf.length, segment.getData().length);
			}
			else 
			{
				segmentBuf = buf;
			}
		}
		return segmentBuf;
	}
	
	public static TCPSegment deserialize(byte[] buf) throws IOException
	{
		TCPSegment segment = null;
		TCPHeader header = null;
		short sourcePort, destinationPort;
		byte[] checkSum = new byte[2];
		byte[] urgentData = new byte[2];
		byte[] data = new byte[576];
		
		ByteBuffer buffer = ByteBuffer.wrap(buf);
		sourcePort = buffer.getShort();
		destinationPort = buffer.getShort();
		
		header = new TCPHeader(sourcePort, destinationPort);
		header.setSequenceNumber(buffer.getInt());
		header.setAckNumber(buffer.getInt());
		header.setOffsetAndFlags(buffer.getShort());
		header.setReceiveWindow(buffer.getShort());
		buffer.get(checkSum);
		header.setInternetChecksum(checkSum);
		buffer.get(urgentData);
		
		segment = new TCPSegment(header);
		if(buffer.limit() != Constants.ACK_SIZE)
		{
			buffer.get(data);
			segment.setData(data);
		}
		
		
		return segment;
	}
}
