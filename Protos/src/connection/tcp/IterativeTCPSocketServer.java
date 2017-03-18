package connection.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import interfaces.ConnectionHandler;

public class IterativeTCPSocketServer {
	private final static int BACKLOG = 50;
	
	private ServerSocket passiveSocket;
	private ConnectionHandler responseHandler;
	
	public IterativeTCPSocketServer(InetAddress localIP, int port, ConnectionHandler responseHandler) throws IOException {
		passiveSocket = new ServerSocket(port, BACKLOG, localIP);
		this.responseHandler = responseHandler;
	}
	
	public void run() throws IOException {
		while (true) {
			Socket dataSocket = passiveSocket.accept();
			responseHandler.handle(dataSocket);
			if (!dataSocket.isClosed())
				dataSocket.close();
		}
	}
}
