import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Enumeration;


public class IPUtils {
	private static String myIP;
	
	static
	{
		myIP = Constants.LOOPBACK;
		getIP();
	}
	
	private static void getIP()
	{
		try {
			Enumeration<NetworkInterface> netEnum = NetworkInterface.getNetworkInterfaces();
			while(netEnum.hasMoreElements())
			{
				NetworkInterface netIntr = netEnum.nextElement();
				Enumeration<InetAddress> inetAddrEnum = netIntr.getInetAddresses();
				while(inetAddrEnum.hasMoreElements())
				{
					String inetAddr = inetAddrEnum.nextElement().getHostAddress();
					if(!inetAddr.equals(Constants.LOOPBACK) &&
							inetAddr.split("\\.").length == 4)
					{
						myIP = inetAddr;
						break;
					}
				}
			}
		} catch (SocketException e) {
			System.err.println("Could not get IP Address. Using loopback.");
		}
	}
	
	public static String getMyIP() {
		if(myIP.equals(Constants.LOOPBACK))
			getIP();
		return myIP;
	}
	
	public static String getUniqueKey(String ipAddress, int port)
	{
		return MessageFormat.format(Constants.ADDRESS_FORMAT,
				ipAddress, port);
	}
}
