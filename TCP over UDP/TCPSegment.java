import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class TCPSegment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCPHeader header;
	private byte[] data;
	
	public TCPSegment(TCPHeader header)
	{
		this.header = header;
		data = new byte[Constants.MAXIMUM_SEGMENT_SIZE];
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public TCPHeader getHeader()
	{
		return header;
	}
				
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.header.toString());
		try {
			if(data != null)
				builder.append(new String(this.data, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return builder.toString();
	}
}
