package nio.http.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ClientToHostCopyHttpProcessor implements HttpProcessor {

	private static final int NGINX_PORT = 8081;
	
	@Override
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey key) {
		outputBuffer.put(inputBuffer);
		HttpConnectionAttributes clientAttributes = (HttpConnectionAttributes) key.attachment();
		ByteBuffer clientWriteBuffer = clientAttributes.getWriteBuffer();
		
		try {
			SocketChannel socket = SocketChannel.open();
			socket.configureBlocking(false);
			
			HttpConnectionAttributes serverAttributes = new HttpConnectionAttributes(ByteBuffer.allocate(4096), outputBuffer, clientWriteBuffer, new ServerToClientCopyProcessor());
			serverAttributes.setConnectedPeerKey(key);
			SelectionKey serverKey;
			
			if (socket.connect(new InetSocketAddress("localhost", NGINX_PORT)))
				serverKey = socket.register(key.selector(), SelectionKey.OP_WRITE, serverAttributes);
			else
				serverKey = socket.register(key.selector(), SelectionKey.OP_CONNECT, serverAttributes);  // OJO: se crean atributos aunq no se logró la conexión
			
			clientAttributes.setConnectedPeerKey(serverKey);
		} catch (IOException e) {
			e.printStackTrace();
			// mandar response de error al cliente de no se pudo conectar
		}
	}

	@Override
	public void processWrite(ByteBuffer writeBuffer, SelectionKey key) {
		SocketChannel socket = (SocketChannel) key.channel();
		try {
			socket.write(writeBuffer);
			socket.close();
		} catch (IOException e) {
			// TODO No se pudo escribir al servidor
			e.printStackTrace();
		}
	}
	
}
