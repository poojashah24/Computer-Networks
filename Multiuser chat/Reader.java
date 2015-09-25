import java.io.BufferedReader;

public class Reader extends Thread {
	
	private BufferedReader reader;
	private boolean stopped;
	
	public Reader(BufferedReader reader) {
		this.reader = reader;
		stopped = false;
	}
	
	public void stopThread()
	{
		stopped = true;
		this.interrupt();
	}
	
	@Override
	public void run() {
		String ip;
		boolean newLine = false;
		boolean noPrompt = false;
		boolean noPrefixPrompt = false;
		
		try
		{
			while(!stopped)
			{
					if((ip = reader.readLine()) != null)
					{
						newLine = false;
						noPrompt = false;
						noPrefixPrompt = false;
						
						if(ip.endsWith("EOL"))
						{
							ip = ip.substring(0, ip.length() - 3);
							newLine = true;
						}
						if(ip.startsWith("NOP"))
						{
							ip = ip.substring(3);
							noPrompt = true;
							
						}
						if(ip.startsWith("NPP"))
						{
							ip = ip.substring(3);
							noPrefixPrompt = true;
							
						}
						if(noPrefixPrompt)
							System.out.print(ip);
						else
							System.out.print(">"+ip);
						if(newLine)
						{
							System.out.println();
							if(!noPrompt)
								System.out.print(">");
						}
						
					}
					else {
						stopped = true;
					}
			}
		}
		catch(Exception e)
		{
			if(!stopped)
			{
				System.err.println("Error occurred while reading: "+e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	
	private void printMsg(String msg)
	{
		if(msg.startsWith("NOP"))
		{
			
		}
		else
			System.out.println();
	}

}
