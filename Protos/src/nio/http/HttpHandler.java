package nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import nio.NioConnectionHandler;
import nio.NioInputBridge;

public class HttpHandler implements NioConnectionHandler {
	
	private static final Charset CHARSET = Charset.forName("ASCII");
	private static final int BUFFER_SIZE = 4096;
	private static final byte[] PERSISTENT_CONNECTION = CHARSET.encode("Connection: keep-alive\r\n").array();

	private String currentlyConnectedServer = "";
	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	private ByteBuffer answerBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private SocketChannel clientChannel;
	private SocketChannel serverChannel;
	private int hostChange = 0;
	private int requestNumber = 0;
	
	public void handle(SocketChannel channel) {
		clientChannel = channel;
		try {
			while (channel.read(buffer) != -1) {
				System.out.println(requestNumber++);
				buffer.flip();
				if (connectToHost()) {
					buffer.rewind();
					generateAnswer();
					answerBuffer.flip();
					while (answerBuffer.hasRemaining())
						serverChannel.write(answerBuffer);
					answerBuffer.clear();
					buffer.clear();
				}
				else {
					buffer.compact();
				}
			}
			channel.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean connectToHost() throws IOException {
		if (buffer.get() != 'G')
			return false;
		
		while (buffer.remaining() > 9 && (buffer.get() != 'H' || buffer.get(buffer.position()) != 'o' || buffer.get(buffer.position()+1) != 's' 
				|| buffer.get(buffer.position()+2) != 't' || buffer.get(buffer.position()+3) != ':' || buffer.get(buffer.position()+4) != ' '))
			;
		
		if (buffer.remaining() <= 9)
			return false;
		
		buffer.position(buffer.position()+5);
		
		int beginningPos = buffer.position();
		
		while (buffer.get() != '\n')
			;
		
		int endPos = buffer.position() - 3;
		int length = endPos - beginningPos + 1;
		
		String hostname = new String (buffer.array(), beginningPos, length, Charset.forName("ASCII"));
		
		if (!hostname.equals(currentlyConnectedServer)) {
			System.out.println("Replacing host " + hostChange++);
			currentlyConnectedServer = hostname;
			serverChannel = SocketChannel.open(new InetSocketAddress(hostname, 80));
			new Thread(new NioInputBridge(serverChannel, clientChannel)).start();
		} 
		
		return true;
	}
	
	// Considerar: otra forma podría ser enviar solo lo que cambia y luego lo que queda igual enviarlo desde buffer
	private void generateAnswer() {
		lineCopy(answerBuffer, buffer);  // GET http://.... HTTP/1.1\r\n
		answerBuffer.put(PERSISTENT_CONNECTION); // Connection: keep-alive
		while (buffer.hasRemaining()) {
			int pos = buffer.position();
			if (buffer.get(pos) != 'P' || buffer.get(pos+1) != 'r' || buffer.get(pos+2) != 'o' || buffer.get(pos+3) != 'x')
				lineCopy(answerBuffer, buffer);
			else {
				while (buffer.get() != '\n')
					;
				answerBuffer.put(buffer);
			}
		}
		
	}
	
	private void lineCopy(ByteBuffer dest, ByteBuffer src) {
		byte b = 0;
		while (src.hasRemaining() && (b = src.get()) != '\n')
			dest.put(b);
		if (b == '\n')
			dest.put(b);
	}

}
