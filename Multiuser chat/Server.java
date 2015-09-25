import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Startup class for the server application. 
 * This listens for incoming connection requests.
 * @author Pooja
 *
 */

public class Server {

	private static ServerSocket socket;
	private ExecutorService executor;
	private List<Future> futures = null;

	public Server() {
		executor = Executors.newFixedThreadPool(100,
				Executors.defaultThreadFactory());
		futures = new ArrayList<Future>();

	}

	private void init(int port) throws IOException {
		socket = new ServerSocket(port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try
				{
					System.out.println();
					System.out.println(Constants.SERVER_OFFLINE);
					ClientConnectionHandler.stop();
					executor.shutdownNow();
					socket.close();
				}
				catch(Exception e)
				{
					System.err.println("Error occurred while shutting down server. ");
					e.printStackTrace();
				}
				
			}
		});
		System.out.println(Constants.SERVER_STARTED);
	}

	private void start() throws IOException {
		while (true) {
			Socket newSocket = socket.accept();
			System.out.println("Connection established");
			futures.add(executor.submit(new ClientConnectionHandler(newSocket)));
		}
	}

	public static void main(String[] args) {

		if (args == null || args.length == 0)
			System.out.println(Constants.NO_PORT);
		else {
			try {
				int port = Integer.parseInt(args[0]);
				Server server = new Server();
				server.init(port);
				server.start();
			} catch (Exception e) {
				if(socket == null || !socket.isClosed())
				{
					System.err.println("Exception occurred:" + e.getMessage());
					e.printStackTrace();

				}
			}

		}
	}
}
