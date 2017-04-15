package nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioInputBridge implements Runnable {

	private static int DEFAULT_BUFFER_SIZE = 4096;
	
	private SocketChannel inputChannel;
	private SocketChannel outputChannel;
	private ByteBuffer buffer;
	
	public NioInputBridge(SocketChannel inputChannel, SocketChannel outputChannel, int bufferSize) {
		this.inputChannel = inputChannel;
		this.outputChannel = outputChannel;
		buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}
	
	@Override
	public void run() {
		try {
			tryRun();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tryRun() throws IOException {		
		while (inputChannel.read(buffer) != -1) {
			buffer.flip();
			while (buffer.hasRemaining())
				outputChannel.write(buffer);
			buffer.clear();
		}
	}
}
