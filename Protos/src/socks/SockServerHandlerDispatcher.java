package socks;

import java.io.IOException;
import java.net.Socket;

import interfaces.ConnectionHandler;

/**
 * Handler dispatcher in order tonot to share resources which could lead to reace conditions.<br>
 * Other solution could be to just use local variables inside handler and inmutable instance variables.
 */
public class SockServerHandlerDispatcher implements ConnectionHandler {
	public void handle(Socket socket) throws IOException {
		new SockServerHandler().handle(socket);
	}
}
