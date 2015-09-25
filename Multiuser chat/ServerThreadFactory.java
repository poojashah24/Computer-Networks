import java.util.concurrent.ThreadFactory;

public class ServerThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}

}
