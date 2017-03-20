package socks;

import java.io.IOException;
import java.net.InetAddress;

import connection.tcp.ThreadedSocketServer;

public class SockServer extends ThreadedSocketServer {
	private final static int DEFAULT_PORT = 1080;
	private final static String DEFAULT_HOST = "localhost";
	
	public SockServer() throws IOException {
		super(InetAddress.getByName(DEFAULT_HOST), DEFAULT_PORT, new SockServerHandlerDispatcher());
	}	
}
