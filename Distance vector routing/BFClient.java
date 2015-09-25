import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class BFClient {
	
	static BFNode bfNode = null;
	private static BufferedReader inputReader;
	private static String inputCommand;
	private static boolean done = false;
	
	
	public static void main(String[] args) throws IllegalArgumentException, SocketException{
		
		validateAndParseNodeDetails(args);
		bfNode.initializeNeighbours(validateAndParseNeighbourDetails(args));
		bfNode.start();
		inputReader = new BufferedReader(new InputStreamReader(System.in));
		
		//This is to handle Ctrl+C
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try
				{
					System.out.println("Node is going offline now");
				}
				catch(Exception e)
				{
					System.err.println("Error occurred while shutting down node. ");
					e.printStackTrace();
				}
				
			}
		});
				
		try
		{
			System.out.print(">");
			while (!done) {
				if (inputReader.ready()) {
					inputCommand = inputReader.readLine();
					Command command = parseCommand(inputCommand);
					if(command != null)
						command.execute();
					System.out.print(">");
				}
			}
		}
		catch (IOException ioe)
		{
			System.err.println("Error occurred reading user input!");
		}
		System.exit(0);
	}
	
	private static Command parseCommand(String input)
	{
		Command command = null;
		String cmd = null;
		String[] cmdArgs = input.split(Constants.SPACE);
		
		cmd = cmdArgs[0];
		if(!ValidationUtils.validateCommand(cmd.toUpperCase()))
		{
			System.err.println(Constants.INVALID_COMMAND);
			return command;
		}
		
		CommandEnum cmdEnum = CommandEnum.valueOf(cmd.toUpperCase().trim());
		switch(cmdEnum)
		{
			case SHOWRT:
				command = new ShowRTCommand();
				break;
		
			case LINKUP:
				if(cmdArgs.length < 3)
					System.err.println(Constants.INSUFFICIENT_PARAMETERS);
				else
				{
					command = new LinkUpCommand(IPUtils.getMyIP(), DVState.getMyPort(), false);
					command.senderIP = cmdArgs[1];
					command.senderPort = Integer.parseInt(cmdArgs[2]);
				}
				break;
				
			case LINKDOWN:
				if(cmdArgs.length < 3)
					System.err.println(Constants.INSUFFICIENT_PARAMETERS);
				else
				{
					command = new LinkDownCommand(IPUtils.getMyIP(), DVState.getMyPort(), false);
					command.senderIP = cmdArgs[1];
					command.senderPort = Integer.parseInt(cmdArgs[2]);
				}
				break;
				
			case CLOSE:
				command = new CloseCommand();
				done = true;
				break;		
				
			case HELP:
				command = new HelpCommand();
				break;
				
			case ROUTE_UPDATE:
				System.err.println(Constants.INVALID_COMMAND);
				break;
		}
		return command;
	}
	
	private static void validateAndParseNodeDetails(String[] args) throws IllegalArgumentException
	{
		if(args.length < 2)
		{
			throw new IllegalArgumentException(Constants.TOO_FEW_ARGUMENTS + "\n" + Constants.USAGE);
		}
		
		if(!ValidationUtils.validateNumber(args[0]))
			throw new IllegalArgumentException(Constants.INVALID_PORT);
		if(!ValidationUtils.validateNumber(args[1]))
			throw new IllegalArgumentException(Constants.INVALID_TIMEOUT);
		
		try
		{
			bfNode = new BFNode(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}
		
	private static List<Neighbor> validateAndParseNeighbourDetails(String args[])
	{
		List<Neighbor> neighbours = new ArrayList<Neighbor>();
		
		for(int i=2; i<args.length;i+=3)
		{
			Neighbor neighbour = ValidationUtils.parseThreeTuple(args[i], args[i+1], args[i+2]);
			if(neighbour != null)
			{
				neighbours.add(neighbour);
			}
		}
		return neighbours;
	}
	
}
