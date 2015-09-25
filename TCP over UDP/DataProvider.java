import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;


public class DataProvider {

	String fileName;
	byte[] buffer;
	TCPSender sender;
	LogManager logManager;
	
	public DataProvider(String filename, String remoteIP, short remotePort, short ackPort,
			LogManager logManager) throws SocketException
	{
		this.fileName = filename;
		this.logManager = logManager;
		buffer = new byte[Constants.MAXIMUM_SEGMENT_SIZE];
		sender = new TCPSender(remoteIP, remotePort, ackPort, logManager);
	}
	
	public void sendFile()
	{
		sendData();	
	}
	
	private void sendData()
	{
		FileInputStream fileStream = null;
		
		try
		{
			fileStream = new FileInputStream(fileName);
			while(fileStream.read(buffer) >= 0)
			{
				sender.sendData(buffer);
				buffer = new byte[Constants.MAXIMUM_SEGMENT_SIZE]; 
			}
			sender.sendData(null);
		}
		catch (FileNotFoundException fne) {
			System.err.println(Constants.FILE_NOT_FOUND);
			logManager.logMessage(Constants.FILE_NOT_FOUND);
		}
		catch (IOException e) {
			logManager.logMessage(Constants.ERROR_READING_FILE);
		}
		finally
		{
			try {
				if(fileStream != null)
					fileStream.close();	
			} catch (IOException e) {
				logManager.logMessage(Constants.ERROR_READING_FILE);
			}
		}
	}
}
