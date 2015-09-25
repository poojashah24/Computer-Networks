public class Constants {
	public static final int MAXIMUM_SEGMENT_SIZE = 576;
	public static final double ALPHA = 0.125f;
	public static final double BETA = 0.25f;
	public static final String ENCODING = "UTF-8";
	public static final int HEADER_LENGTH = 20;
	public static final int RECEIVER_BUFFER_SIZE = 596;
	public static final int ACK_SIZE = 20;
	

	public static final String INVALID_PARAMETERS = "Invalid parameters!";
	public static final String USAGE = "sender <filename> <remote_IP> <remote_port> <ack_port_num> <log_filename>";
	public static final String INVALID_PORT_NUMBER = "Port number should be an integer between 0 and 65535";
	public static final String INVALID_IP_ADDRESS = "Invalid IP address";
	public static final String FILE_NOT_FOUND = "File to be sent not found!";
	public static final String SOCKET_EXCEPTION = "Could not open socket";
	public static final String ERROR_READING_FILE = "Error occurred while reading file";
	public static final String ERROR_WRITING_FILE = "Error occurred while writing to file";
	public static final String INVALID_LOG_FILE = "Log file could not be initialized. Console logging will be enabled";
	public static final String SUCCESS = "Delivery completed successfully";
	public static final String TOTAL_BYTES = "Total bytes sent: ";
	public static final String TOTAL_SEGMENT = "Segments sent: ";
	public static final String RETRANSMISSION = "Segments retransmitted: ";
	public static final String TIMEOUT = "Timeout occurred for packet: ";
	public static final String INTERRUPTED_EXCEPTION = "Interrupted exception occurred at sender";
	public static final String FILE_CREATION_FAILED = "Could not create new file!";
	public static final String ACK_LISTEN_ERROR = "Exception occurred while listening for ACKs";
	
	public static final String IPADDRESS_REGEX = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	public static final String SOURCE = " Source: ";
	public static final String DESTINATION = " Destination: ";
	public static final String SEQUENCE_NO = " Sequence #: ";
	public static final String ACK_NO = " ACK #: ";
	public static final String ACK = " ACK: " ;
	public static final String FIN = " FIN: " ;
	public static final String SEPERATOR = ":";
	public static final String RECEIVED = " Received ";
	public static final String SENT = " Sent ";
	public static final String ESTIMATED_RTT = " EstimatedRTT: ";
	
	
}
