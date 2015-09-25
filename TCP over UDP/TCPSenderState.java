package sender;

/**
 * The TCP sender will be in one of the states at any point of time.
 * @author Pooja
 *
 */
public enum TCPSenderState {
	WAITING_FOR_DATA, SENDING_DATA, WAITING_FOR_ACK, TIMED_OUT
}
