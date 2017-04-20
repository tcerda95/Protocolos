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
			socketChannel.register(key.selector(), SelectionKey.OP_READ, new HttpConnectionAttributes(bufSize, new ClientToHostCopyHttpProcessor())); // Leer del cliente
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

	public void handleConnect(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			if (socketChannel.finishConnect())
				key.interestOps(SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
			return;
			// TODO: response de error al cliente de que no se pudo conectar al servidor
		}
	}

	public void handleWrite(SelectionKey key) {
		HttpConnectionAttributes attributes = (HttpConnectionAttributes) key.attachment();
		attributes.processWrite(key);
	}

}
