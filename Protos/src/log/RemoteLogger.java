package log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import interfaces.CodingMethod;
import interfaces.Log;
import reader.CustomSeparatorReader;

public class RemoteLogger implements Log {
	private static final String READ_REQUEST = "READ";
	private static final String WRITE_REQUEST = "WRITE";
	private static final String END_READ = "END";

	private InetAddress address;
	private int port;
	private String filename;
	private CodingMethod codingMethod;
	
	public RemoteLogger(InetAddress address, int port, String filename, CodingMethod codingMethod) {
		this.address = address;
		this.port = port;
		this.filename = filename;
		this.codingMethod = codingMethod;
	}
	
	@Override
	public void warning(String msg) {
		Message message = new SimpleMessage(msg, Level.warning);
		write(message);
	}

	@Override
	public void info(String msg) {
		Message message = new SimpleMessage(msg, Level.info);
		write(message);
	}

	@Override
	public void log(Message msg) {
		write(msg);
	}
	
	private void write(Message message) {
		String encodedMsg = encodeMsg(message);
		trySendEncodedMessage(encodedMsg);
	}
	
	private String encodeMsg(Message message) {
		StringBuilder builder = new StringBuilder(WRITE_REQUEST + " " + filename);
		builder.append(codingMethod.getSeparator());
		builder.append(codingMethod.encode(message));
		return builder.toString();
	}

	private void trySendEncodedMessage(String encodedMessage) {
		try {
			sendMessage(encodedMessage);			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void sendMessage(String encodedMessage) throws UnknownHostException, IOException {
		Socket socket = buildSocket();
		OutputStream output = socket.getOutputStream();
		
		output.write(encodedMessage.getBytes(codingMethod.getCharsetName()));
		socket.close();
	}

	private Socket buildSocket() throws IOException {
		return new Socket(address, port);
	}

	@Override
	public void read(InputStream input, Consumer<Message> consumer) throws IOException {
		// TODO Auto-generated method stub

	}

	public void remoteRead(Consumer<Message> consumer) throws IOException {
		Socket socket = buildSocket();
		sendReadRequest(socket);
		consumeMessages(socket, consumer);
		socket.close();
	}

	private void sendReadRequest(Socket socket) throws IOException {
		OutputStream output = socket.getOutputStream();
		String request = READ_REQUEST + " " + filename + codingMethod.getSeparator();
		output.write(request.getBytes(codingMethod.getCharsetName()));
	}
	
	private void consumeMessages(Socket socket, Consumer<Message> consumer) throws UnsupportedEncodingException, IOException {
		@SuppressWarnings("resource")
		CustomSeparatorReader reader = new CustomSeparatorReader(socket.getInputStream(), 
				codingMethod.getCharsetName(), codingMethod.getSeparator());
		
		String line;
		while (!(line = reader.readIncludeSeparator()).startsWith(END_READ))
			consumer.accept(codingMethod.decode(line));
	}
}
