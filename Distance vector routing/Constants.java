public class Constants {
	public static final String DVLIST = " Distance Vector list is:";
	public static final String DESTINATION = "Destination = ";
	public static final String COST = "Cost = ";
	public static final String LINK = "Link = ";
	public static final String COMMA = ", ";
	public static final String ADDRESS_FORMAT = "{0}:{1}";
	public static final String LINK_FORMAT = "({0}:{1})";
	public static final String COST_FORMAT = "%.2f";

	public static final String IPADDRESS_REGEX = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	public static final String NUMBER_REGEX = "[0-9]+(\\.[0.9])*";
	public static final String SPACE = " ";

	public static final String INVALID_IP = "Please enter a valid IP address";
	public static final String INVALID_PORT = "Please enter a valid port number";
	public static final String INVALID_COST = "Please enter a valid link cost";
	public static final String INVALID_TIMEOUT = "Please enter a valid timeout value";
	public static final String INVALID_COMMAND = "Invalid command. For a list of commands, type <help>";
	public static final String TOO_FEW_ARGUMENTS = "Too few arguments entered!";
	public static final String USAGE = "./bfclient <localport> <timeout> [ipaddress1 port1 weight1]...";
	public static final String PARSE_ERROR = "Error parsing input";
	public static final String SOCKET_ERROR = "Could not establish connection to {0}:{1}";
	public static final String SEND_ERROR = "Error sending distance vector to neighbors";
	public static final String NO_NEIGHBOUR = "No neighbor with that ip address/port combination exists";

	public static final String IP = "ipaddress";
	public static final String PORT = "port";
	public static final String SENDER_IP = "sender_ipddress";
	public static final String SENDER_PORT = "sender_port";
	public static String WEIGHT = "weight";
	public static String COMMAND = "command";

	public static final String LOOPBACK = "127.0.0.1";
	public static final int BUFFER_SIZE = 1024;
	public static final String ENCODING = "UTF-8";

	public static final String LINKDOWN = "Breaks a link. Usage: LINKDOWN <ipaddress> <port>";
	public static final String LINKUP = "Brings up a link. Usage: LINKDOWN <ipaddress> <port>";
	public static final String SHOWRT = "Display the routing table. Usage: SHOWRT";
	public static final String CLOSE = "Shutdown the node. Usage: CLOSE";
	public static final String INSUFFICIENT_PARAMETERS = "Insufficient Parameters! Type <help> for command usage";
}
