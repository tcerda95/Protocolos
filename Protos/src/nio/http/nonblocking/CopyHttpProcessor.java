package nio.http.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class CopyHttpProcessor implements HttpProcessor {

	private static final int NGINX_PORT = 8081;
			
	@Override
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey key) {
		outputBuffer.put(inputBuffer);
		
		try {
			SocketChannel socket = SocketChannel.open();
			socket.configureBlocking(false);
			if (socket.connect(new InetSocketAddress("localhost", NGINX_PORT)))
				socket.register(key.selector(), SelectionKey.OP_WRITE, new HttpConnectionAttributes(outputBuffer, ByteBuffer.allocate(4096), new ServerToClientCopyProcessor()));
			else
				socket.register(key.selector(), SelectionKey.OP_CONNECT, new HttpConnectionAttributes(outputBuffer, ByteBuffer.allocate(4096), new ServerToClientCopyProcessor()));
				
		} catch (IOException e) {
			e.printStackTrace();
			// mandar response de error al cliente de no se pudo conectar
		}
	}
	
}
