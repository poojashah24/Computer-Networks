/**
 * This class represents a message sent to an offline user
 * @author Pooja
 *
 */
public class OfflineMessage {
	private ChatUser sender;
	private String message;
	
	public OfflineMessage(ChatUser sender, String msg)
	{
		this.sender = sender;
		this.message = msg;
	}

	public ChatUser getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
	
	
}
