package nio.http.nonblocking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ServerToClientCopyProcessor implements HttpProcessor {

	@Override
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey selectionKey) {
		outputBuffer.put(inputBuffer);
		HttpConnectionAttributes serverAttributes = (HttpConnectionAttributes) selectionKey.attachment();
		SelectionKey clientKey = serverAttributes.getConnectedPeerKey();
		
		clientKey.interestOps(SelectionKey.OP_WRITE); // OJO: el cliente podría estar anotado para read
		try {
			selectionKey.channel().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void processWrite(ByteBuffer writeBuffer, SelectionKey key) {
		SocketChannel socket = (SocketChannel) key.channel();
		try {
			socket.write(writeBuffer);
			key.interestOps(SelectionKey.OP_READ);
		} catch (IOException e) {
			// TODO No se pudo escribir al servidor
			e.printStackTrace();
		}
	}

}
