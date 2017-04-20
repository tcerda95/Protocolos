package nio;

import java.nio.channels.SocketChannel;

public class NioFullDuplex {
	
	private static final int DEFAULT_BUFFER_SIZE = 4096;
	
	private final SocketChannel leftChannel;
	private final SocketChannel rightChannel;
	private final int bufferSize;
	
	public NioFullDuplex(SocketChannel leftChannel, SocketChannel rightChannel, int bufferSize) {
		this.leftChannel = leftChannel;
		this.rightChannel = rightChannel;
		this.bufferSize = bufferSize;
	}
	
	public NioFullDuplex(SocketChannel leftChannel, SocketChannel rightChannel) {
		this(leftChannel, rightChannel, DEFAULT_BUFFER_SIZE);
	}

	public void run() throws InterruptedException {
		NioInputBridge leftToRight = new NioInputBridge(leftChannel, rightChannel, bufferSize);
		NioInputBridge rightToLeft = new NioInputBridge(rightChannel, leftChannel, bufferSize);
		
		Thread t1 = new Thread(leftToRight);
		Thread t2 = new Thread(rightToLeft);
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
	}
}
