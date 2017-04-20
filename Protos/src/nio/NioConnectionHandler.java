package nio;

import java.nio.channels.SocketChannel;

public interface NioConnectionHandler {
	public void handle (SocketChannel channel);
}
