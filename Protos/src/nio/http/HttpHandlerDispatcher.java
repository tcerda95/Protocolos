package nio.http;

import java.nio.channels.SocketChannel;

import nio.NioConnectionHandler;

public class HttpHandlerDispatcher implements NioConnectionHandler {

	@Override
	public void handle(SocketChannel channel) {
		new HttpHandler().handle(channel);
	}

}
