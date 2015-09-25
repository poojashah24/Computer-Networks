import java.nio.ByteBuffer;

public class ChecksumHelper {
	public static final boolean validateChecksum(TCPSegment segment)
	{
		TCPHeader header = segment.getHeader();
		byte[] checksum = header.internetChecksum;
		short computedChecksum = computeChecksum(segment);
		short headerChecksum = ByteBuffer.allocate(2).wrap(checksum).getShort();
		
		return computedChecksum == headerChecksum;
	}
	
	public static short computeChecksum(TCPSegment segment)
	{
		int i = 0, bytes = 0;
		short checksum = 0;
		int upperBytes, lowerBytes;
		byte[] buf = getBytes(segment);
		int length = buf.length;
		
		while(length >= 2)
		{
			//Shift the first bytes to form the upper bytes of a 2byte chunk
			upperBytes = buf[i++] << 8 & 0xFF00;
			//Get the lower bytes
			lowerBytes = buf[i++] * 0xFF;
			//OR the upper and lower bytes, and handle carry forward
			bytes = upperBytes | lowerBytes;
			checksum += bytes;
			checksum = handleCarry(checksum);
			length -= 2;
		}
		
		//If the number of 16bit chunks was odd, handle the last one
		if(length > 0)
		{
			upperBytes = buf[i] << 8;
			lowerBytes = 0xFF00;
			bytes = upperBytes | lowerBytes;
			checksum += bytes;
			checksum = handleCarry(checksum);
		}
		
		//1's complement sum
		checksum = (short) ~checksum;
		checksum = (short) (checksum & 0xFFFF);
		
		return checksum;
	}
	
	public static TCPSegment computeAndAddChecksum(TCPSegment segment)
	{
		segment.getHeader().setInternetChecksum(ByteBuffer.allocate(2).putShort(computeChecksum(segment)).array());	
		return segment;
	}
	
	private static short handleCarry(short sum)
	{
		if ((sum & 0xFFFF0000) > 0)
		{
			sum = (short) (sum & 0xFFFF);
			sum += 1;
		}
		return sum;
	}
	
	private static byte[] getBytes(TCPSegment segment)
	{
		TCPHeader header = segment.getHeader();
		
		ByteBuffer buffer = ByteBuffer.allocate(596);
		buffer.putShort(header.sourcePortNumber);
		buffer.putShort(header.destinationPortNumber);
		buffer.putShort(header.offsetAndFlags);
		buffer.putShort(header.receiveWindow);
		buffer.put(segment.getData());
		
		return buffer.array();
	}
}
