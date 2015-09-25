import java.util.Arrays;

public class Utils {

	public static byte[] trimBuffer(byte[] buf)
	{
		if(buf != null)
		{
			int i = buf.length - 1;
			while(buf[i] == 0 && i >= 0)
			{
				i--;
			}
			return Arrays.copyOf(buf, i+1);
		}
		return null;
	}
}
