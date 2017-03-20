package connection.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import interfaces.ConnectionHandler;

public class IterativeSocketServer implements Runnable {
	private final static int BACKLOG = 50;
	
	private ServerSocket passiveSocket;
	private ConnectionHandler responseHandler;
	
	public IterativeSocketServer(InetAddress address, int port, ConnectionHandler responseHandler) throws IOException {
		this(new ServerSocket(port, BACKLOG, address), responseHandler);
	}
	
	public IterativeSocketServer(ServerSocket passiveSocket, ConnectionHandler responseHandler) throws IOException {
		this.passiveSocket = passiveSocket;
		this.responseHandler = responseHandler;
	}
	
	public void run() {
		while (true) {
			Socket dataSocket;
			try {
				dataSocket = passiveSocket.accept();
				responseHandler.handle(dataSocket);
				if (!dataSocket.isClosed())
					dataSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
