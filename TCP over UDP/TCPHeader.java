import java.io.Serializable;

public class TCPHeader implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 16-bit source port number
	short sourcePortNumber;
	// 16 bit destination port number
	short destinationPortNumber;

	// 32-bit sequence number
	int sequenceNumber;

	// 32-bit acknowledgment number
	int ackNumber;

	// 16-bit receive window
	short receiveWindow;

	// 16-bit checksum
	byte[] internetChecksum;
	
	short offsetAndFlags;

	private TCPHeader() {
		/*This 16 bit value represents the offset(header length), the unused bits and the TCP flags*/
		offsetAndFlags = 20480;
	}

	public TCPHeader(short srcPortNumber, short destPortNumber) {
		this();
		this.sourcePortNumber = srcPortNumber;
		this.destinationPortNumber = destPortNumber;
	}

	public short getSourcePortNumber() {
		return sourcePortNumber;
	}

	public short getDestinationPortNumber() {
		return destinationPortNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setAckNumber(int ackNumber) {
		this.ackNumber = ackNumber;
	}

	public void setAck() {
		offsetAndFlags = BitHelper.setAck(offsetAndFlags);
	}

	public void setFin() {
		offsetAndFlags = BitHelper.setFin(offsetAndFlags);
	}

	public void setReceiveWindow(short receiveWindow) {
		this.receiveWindow = receiveWindow;
	}

	public void setInternetChecksum(byte[] checksum) {
		this.internetChecksum = checksum;
	}
	
	public boolean isFIN()
	{
		return BitHelper.isFin(offsetAndFlags);
	}
	
	public boolean isACK()
	{
		return BitHelper.isAck(offsetAndFlags);
	}
	
	public int getSequenceNumber()
	{
		return sequenceNumber;
	}
	
	public int getAckNumber()
	{
		return ackNumber;
	}
	
	public byte[] getInternetChecksum() {
		return internetChecksum;
	}

	public void setOffsetAndFlags(short offsetAndFlags)
	{
		this.offsetAndFlags = offsetAndFlags;
	}
	
	public short getOffsetAndFlags()
	{
		return offsetAndFlags;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Source Port Number: " + sourcePortNumber + "\n");
		builder.append("Destination Port Number: " + destinationPortNumber + "\n");
		builder.append("Sequence Number: " + sequenceNumber + "\n");
		builder.append("Ack Number: " + ackNumber + "\n");
		builder.append("Receive Window: " + receiveWindow + "\n");
		builder.append("Internet Checksum: " + internetChecksum + "\n");
		
		return builder.toString();
	}
}
