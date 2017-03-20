package connection.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import interfaces.ConnectionHandler;

public class ThreadedPoolSocketServer {
	private final static int BACKLOG = 50;
	private static final int POOL_SIZE = 20;
	
	private ServerSocket serverSocket;
	private ConnectionHandler handler;
	
	public ThreadedPoolSocketServer(InetAddress address, int port, ConnectionHandler handler) throws IOException {
		this.serverSocket = new ServerSocket(port, BACKLOG, address);
		this.handler = handler;
	}
	
	public void run() throws IOException {
		for (int i = 0; i < POOL_SIZE; i++) {
			new Thread(new IterativeSocketServer(serverSocket, handler)).start();
		}
	}
}
