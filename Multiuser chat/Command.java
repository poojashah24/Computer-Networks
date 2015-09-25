/**
 * This enum is used to store all the possible commands
 * @author Pooja
 *
 */
public enum Command {
	LOGOUT(Constants.LOGOUT_DESC), 
	WHOELSE(Constants.WHOELSE_DESC), 
	WHOLASTHR(Constants.WHOLASTHR_DESC), 
	BROADCAST(Constants.BROADCAST_DESC), 
	MESSAGE(Constants.MSG_DESC), 
	BLOCK(Constants.BLOCK_DESC), 
	UNBLOCK(Constants.UNBLOCK_DESC), 
	TAG(Constants.TAG_DESC), 
	UNTAG(Constants.UNTAG_DESC), 
	PRESENCE(Constants.PRESENCE_DESC), 
	HELP(Constants.EMPTY), 
	UNKNOWN(Constants.EMPTY);
	
	private String desc;
	
	private Command(String desc)
	{
		this.desc = desc;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	@Override
	public String toString() {
		return name() + Constants.SEPERATOR + getDescription();
	}
}
