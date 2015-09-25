import java.util.regex.Pattern;

public class ValidationUtils {

	public static Neighbor parseThreeTuple(String ipAddress, String port,
			String cost) throws IllegalArgumentException {
		if (!validateIPAddress(ipAddress))
			throw new IllegalArgumentException(Constants.INVALID_IP);
		if(!validateNumber(port))
			throw new IllegalArgumentException(Constants.INVALID_PORT);
		if(!validateNumber(cost))
			throw new IllegalArgumentException(Constants.INVALID_COST);
		
		return new Neighbor(ipAddress, Integer.parseInt(port), Double.parseDouble(cost));
	}

	public static boolean validateIPAddress(String ip) {
		return Pattern.matches(Constants.IPADDRESS_REGEX, ip);
	}

	public static boolean validateNumber(String num) {
		return Pattern.matches(Constants.NUMBER_REGEX, num);
	}
	
	public static boolean validateCommand(String command)
	{
		boolean valid = false;
		for(CommandEnum cmd : CommandEnum.values())
		{
			if(cmd.name().equals(command))
			{
				valid = true;
				break;
			}
		}
		return valid;
	}
}
