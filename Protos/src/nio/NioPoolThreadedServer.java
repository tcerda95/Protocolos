package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioPoolThreadedServer {

	private static final int POOL_SIZE = 100;
	private static final int BACKLOG = 50;
	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(POOL_SIZE);
	
	private final ServerSocketChannel serverSocketChannel;
	private final NioConnectionHandler connectionHandler;
	
	public NioPoolThreadedServer(int port, NioConnectionHandler connectionHandler) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port), BACKLOG);
		this.connectionHandler = connectionHandler;
	}
	
	public void run() throws IOException {
		while (true) {
			SocketChannel client = serverSocketChannel.accept();
			EXECUTOR.submit(() -> connectionHandler.handle(client));
		}
	}
}
