package socks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import connection.FullDuplex;
import interfaces.ConnectionHandler;

public class SockServerHandler implements ConnectionHandler {
	private static final int BUF_SIZE = 512;
	private static final int VERSION = 5;
	
	private byte[] buffer = new byte[BUF_SIZE];
	private Request request;
	private Socket clientSocket;
	private Socket serverSocket;
	
	@Override
	public void handle(Socket socket) throws IOException {
		clientSocket = socket;
		try {
			processInitialHandshake();
			
			if (clientSocket.isClosed())
				return;
			
			processRequest();
			
			if (clientSocket.isClosed())
				return;

			serverSocket = new Socket(request.getDestinationAddress(), request.getDestinationPort());
			new FullDuplex(clientSocket, serverSocket).run();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (clientSocket != null && !clientSocket.isClosed())
				clientSocket.close();
			
			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();
		}
	}

	private void processInitialHandshake() throws IOException {
		InputStream input = clientSocket.getInputStream();
		OutputStream output = clientSocket.getOutputStream();
		
		Handshake handshake = new Handshake(input);
		replyHandshake(handshake, output);		
	}
	
	private void replyHandshake(Handshake handshake, OutputStream output) throws IOException {
		buffer[0] = (byte) VERSION;
		
		if (validHandshake(handshake)) {
			buffer[1] = AuthorizationMethod.NO_AUTHORIZATION.code();
			output.write(buffer, 0, 2);
		}
		else {
			buffer[1] = AuthorizationMethod.NO_ACCEPTABLE.code();
			output.write(buffer, 0, 2);
			clientSocket.close();
		}		
	}

	private boolean validHandshake(Handshake handshake) {
		return handshake.getVersion() == VERSION && handshake.hasMethod(AuthorizationMethod.NO_AUTHORIZATION);
	}

	private void processRequest() throws IOException {
		InputStream input = clientSocket.getInputStream();
		OutputStream output = clientSocket.getOutputStream();
		
		request = new Request(input);
		replyRequest(request, output);
		
	}

	private void replyRequest(Request request, OutputStream output) throws IOException {
		int reqLen = request.getLen();
		
		byte[] requestBytes = request.getRequestBytes();
		byte tmp = requestBytes[1];
		
		if (validRequest(request)) {
			requestBytes[1] = ServerReply.SUCCEDED.code();
			output.write(requestBytes, 0, reqLen);
		}
		else {
			requestBytes[1] = ServerReply.REFUSED.code();
			output.write(requestBytes, 0, reqLen);
			clientSocket.close();
		}
		
		requestBytes[1] = tmp;
	}
	
	private boolean validRequest(Request request) {
		return request.getVersion() == VERSION && request.getDestinationAddress() != null 
				&& request.getDestinationPort() > 0 && request.getCommand() == ConnectionCommand.CONNECT;
	}
}
