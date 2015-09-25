import java.net.SocketException;
import java.util.regex.Pattern;

public class Sender {
	
	private String fileName;
	private String logFileName;
	private String remoteIP;
	private short remotePort;
	private short ackPort;
	private LogManager logManager;
	private DataProvider dataProvider;
	
	public Sender()
	{
	}
	
	public static void main(String[] args) 
	{		
		if(args == null || args.length < 5)
		{
			System.err.println(Constants.INVALID_PARAMETERS);
			System.err.println(Constants.USAGE);
			return;
		}
		Sender sender = new Sender();	
		boolean validationResult = sender.validateInput(args);
		if(!validationResult)
		{
			return;
		}
		sender.initialize();
	}
	
	private void initialize()
	{
		try
		{
			logManager = new LogManager(logFileName);
			dataProvider = new DataProvider(fileName, remoteIP, remotePort, ackPort, logManager);
			dataProvider.sendFile();
		}
		catch(SocketException se)
		{
			logManager.logMessage(Constants.SOCKET_EXCEPTION + Constants.SEPERATOR + se.getMessage());
		}
	}
	
	private boolean validateInput(String[] args)
	{
		boolean result = true;
		
		fileName = args[0];
		logFileName = args[4];
		remoteIP = args[1];
		
		if(!validateIPAddress(remoteIP))
		{
			System.err.println(Constants.INVALID_IP_ADDRESS);
			result = false;
		}
		try
		{
			remotePort = Short.parseShort(args[2]);
			ackPort = Short.parseShort(args[3]);
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
