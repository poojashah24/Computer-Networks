import java.io.IOException;


public abstract class Command {
	protected CommandEnum commandEnum;
	protected String senderIP;
	protected int senderPort;
	
	
	public CommandEnum getCommandType()
	{
		return commandEnum;
	}
	
	public void setSenderIP(String ip)
	{
		this.senderIP = ip;
	}
	
	public void setSenderPort(int port)
	{
		this.senderPort = port;
	}
	
	public String getSenderIP()
	{
		return senderIP;
	}
	
	public int getSenderPort()
	{
		return senderPort;
	}
	
	
	public abstract void execute() throws IOException;
	
	//This returns a command given a string input
	public static Command getCommand(String input) throws IllegalArgumentException
	{
		String[] args = input.split(Constants.SPACE);
		CommandEnum cmdType = CommandEnum.valueOf(args[0]);
		
		Command command = null;
		switch(cmdType)
		{
			case SHOWRT:
				command = new ShowRTCommand();
				break;
				
			case LINKUP:
				if(args.length < 3)
				{
					throw new IllegalArgumentException(Constants.TOO_FEW_ARGUMENTS);
				}
				if(!ValidationUtils.validateIPAddress(args[1]))
				{
					throw new IllegalArgumentException(Constants.INVALID_IP);
				}
				if(!ValidationUtils.validateNumber(args[2]))
				{
					throw new IllegalArgumentException(Constants.INVALID_PORT);
				}
				command = new LinkUpCommand(args[1], Integer.parseInt(args[2]), false);
				break;
				
			case LINKDOWN:
				if(args.length < 3)
				{
					throw new IllegalArgumentException(Constants.TOO_FEW_ARGUMENTS);
				}
				if(!ValidationUtils.validateIPAddress(args[1]))
				{
					throw new IllegalArgumentException(Constants.INVALID_IP);
				}
				if(!ValidationUtils.validateNumber(args[2]))
				{
					throw new IllegalArgumentException(Constants.INVALID_PORT);
				}
				command = new LinkDownCommand(args[1], Integer.parseInt(args[2]), false);
				break;
				
			case CLOSE:
				command = new CloseCommand();
				break;
				
			default:
					throw new IllegalArgumentException(Constants.INVALID_COMMAND);
		}
		return command;
	}
}
