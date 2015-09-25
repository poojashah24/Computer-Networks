import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class LogManager {
	private String logFileName;
	private static PrintWriter writer;

	public LogManager(String fileName) {
		
		try
		{
			this.logFileName = fileName;
			if(fileName != null && !fileName.trim().isEmpty())
				writer = new PrintWriter(new FileOutputStream(logFileName), true);
			else
				writer = new PrintWriter(System.out);	
		}
		catch (FileNotFoundException fnfe)
		{
			System.err.println(Constants.INVALID_LOG_FILE);
			writer = new PrintWriter(System.out);
		}
		
	}
	
	public void logMessage(String mesg)
	{
		writer.println(mesg);
	}

	public void log(String mode, String source, String dest, int sequenceNo, int ackNo, boolean ack, boolean fin) {
		StringBuilder builder = new StringBuilder();
		builder.append(new Date());
		builder.append(mode);
		builder.append(Constants.SOURCE);
		builder.append(source);
		builder.append(Constants.DESTINATION);
		builder.append(dest);
		builder.append(Constants.SEQUENCE_NO);
		builder.append(sequenceNo);
		builder.append(Constants.ACK_NO);
		builder.append(ackNo);
		builder.append(Constants.ACK);
		builder.append(ack);
		builder.append(Constants.FIN);
		builder.append(fin);
		writer.println(builder.toString());
	}

	public void log(String mode, String source, String dest, int sequenceNo, int ackNo, boolean ack, boolean fin, double estimatedRTT) {
		StringBuilder builder = new StringBuilder();
		builder.append(new Date());
		builder.append(mode);
		builder.append(Constants.SOURCE);
		builder.append(source);
		builder.append(Constants.DESTINATION);
		builder.append(dest);
		builder.append(Constants.SEQUENCE_NO);
		builder.append(sequenceNo);
		builder.append(Constants.ACK_NO);
		builder.append(ackNo);
		builder.append(Constants.ACK);
		builder.append(ack);
		builder.append(Constants.FIN);
		builder.append(fin);
		builder.append(Constants.ESTIMATED_RTT);
		builder.append(estimatedRTT);
		writer.println(builder.toString());
	}

	public void shutDown() {
		if(writer != null)
		{
			writer.flush();
			writer.close();
			writer = null;
		}
		
	}
}
