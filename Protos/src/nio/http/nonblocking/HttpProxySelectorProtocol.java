package nio.http.nonblocking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpProxySelectorProtocol {
	private int bufSize;
	
	public HttpProxySelectorProtocol(int bufSize) {
		this.bufSize = bufSize;
	}
	
	public void handleAccept(SelectionKey key) { 
		try {
			SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(key.selector(), SelectionKey.OP_READ, new HttpConnectionAttributes(bufSize, new CopyHttpProcessor())); // Leer del cliente
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public void handleRead(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		HttpConnectionAttributes attributes = (HttpConnectionAttributes) key.attachment();
		
		ByteBuffer buffer = attributes.getReadBuffer();
		try {
			socketChannel.read(buffer);
		} catch (IOException e) {
			e.printStackTrace(); // cliente/host cerró la conexión y no se puede leer
			return;
		}
		
		// Procesar lo que se leyó 
		attributes.processRead(key);
	}

}
