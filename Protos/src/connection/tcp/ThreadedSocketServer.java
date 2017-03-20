package connection.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.ConnectionHandler;

public class ThreadedSocketServer {
	private final static int BACKLOG = 50;
	
	private ServerSocket passiveSocket;
	private ConnectionHandler handler;
	
	public ThreadedSocketServer(InetAddress address, int port, ConnectionHandler handler) throws IOException {
		passiveSocket = new ServerSocket(port, BACKLOG, address);
		this.handler = handler;
	}
	
	public void run() throws IOException {
		while (true) {
			Socket socket = passiveSocket.accept();
			Thread thread = new Thread(new ClientAttender(socket, handler));
			thread.start();
		}
	}
	
	private static class ClientAttender implements Runnable {
		private Socket socket;
		private ConnectionHandler handler;
		
		public ClientAttender(Socket socket, ConnectionHandler handler) {
			this.socket = socket;
			this.handler = handler;
		}
		
		@Override
		public void run() {
			try {
				handler.handle(socket);
				if (!socket.isClosed())
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
