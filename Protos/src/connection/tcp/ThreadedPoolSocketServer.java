package connection.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import interfaces.ConnectionHandler;

public class ThreadedPoolSocketServer implements Runnable {
	private final static int BACKLOG = 50;
	private static final int POOL_SIZE = 20;
	
	private ServerSocket serverSocket;
	private ConnectionHandler handler;
	
	public ThreadedPoolSocketServer(InetAddress address, int port, ConnectionHandler handler) throws IOException {
		this.serverSocket = new ServerSocket(port, BACKLOG, address);
		this.handler = handler;
	}
	
	public void run() {
		for (int i = 0; i < POOL_SIZE; i++) {
			try {
				new Thread(new IterativeSocketServer(serverSocket, handler)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
