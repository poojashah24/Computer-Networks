import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Client {

	// Do not make all of this static!
	private static Socket socket;
	private static PrintWriter writer;
	private static BufferedReader reader;
	private static BufferedReader inputReader;
	private static Reader readerThread;
	

	public static void main(String[] args) {
		//Initialize some default values for the chat server connections
		String ipaddress = "127.0.0.1";
		int port = 6000;
		try {
			
			if (args == null || args.length < 2) {
				System.out.print("Please enter the IP Address / Port Number of the server\n");
				System.exit(0);
			}
			else 
			{
				try
				{
					ipaddress = args[0];
					port = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException ne)
				{
					System.err.println("Please enter a valid port number\n");
					System.exit(0);
				}
			}
			
			socket = new Socket(ipaddress, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			readerThread = new Reader(reader);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try
					{
						System.out.println();
						System.out.println("Client is going offline now");
						readerThread.stopThread();
						socket.close();
					}
					catch(Exception e)
					{
						System.err.println("Error occurred while shutting down server. ");
						e.printStackTrace();
					}
					
				}
			});

			readerThread.start();

			while (true) {
				if (inputReader.ready()) {
					writer.println(inputReader.readLine());
				}
				if(!readerThread.isAlive())
				{
					break;
				}
			}
			System.exit(0);

		}
		catch(ConnectException ce)
		{
			System.err.println("The server is not online.");
		}
		catch (Exception e) {
			System.err.println("Error:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
