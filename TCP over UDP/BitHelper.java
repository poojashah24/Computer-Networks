public class BitHelper {

	public static final short FIN_MASK = 1;
	public static final short ACK_MASK = 16;
	
	public static short setFin(short offsetFlags)
	{
		return (short) ((short) offsetFlags | FIN_MASK);
	}
	
	public static boolean isFin(short offsetFlags)
	{
		boolean result = false;
		short value = (short) ((short) offsetFlags & FIN_MASK);
		result = (value == FIN_MASK) ? true : false;
		return result;
	}
	
	public static short setAck(short offsetFlags)
	{
		return (short) ((short) offsetFlags | ACK_MASK);
	}
	
	public static boolean isAck(short offsetFlags)
	{
		boolean result = false;
		short value = (short) ((short) offsetFlags & ACK_MASK);
		result = (value == ACK_MASK) ? true : false;
		return result;
	}
}
