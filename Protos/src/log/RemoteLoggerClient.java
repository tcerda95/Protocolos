package log;

import java.io.IOException;
import java.net.InetAddress;
import interfaces.Log.Message;

public class RemoteLoggerClient {
	private static final int PORT = 20000;
	private static final String hostname = "localhost";

	public static void main(String[] args) throws IOException {
		RemoteLogger logger = new RemoteLogger(InetAddress.getByName(hostname), PORT, "src/log/remoteLog.txt", 
				new MyCodingMethod());
		
		logger.warning("alto warning remoto");
		logger.warning("info remoto con informacion");
		
		logger.remoteRead((Message m) -> System.out.println(m));
	}

}
