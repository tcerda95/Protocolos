package nio.http.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class HttpConnectionAttributes {	
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private ByteBuffer processedBuffer;
	private HttpProcessor processor;
	private SelectionKey connectedPeerKey;
	
	public HttpConnectionAttributes(ByteBuffer readBuffer, ByteBuffer writeBuffer, ByteBuffer processedBuffer, HttpProcessor processor) {
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
		this.processedBuffer = processedBuffer;
		this.processor = processor;
	}
	
	public HttpConnectionAttributes(int bufSize, HttpProcessor processor) {
		this(ByteBuffer.allocate(bufSize), ByteBuffer.allocate(bufSize), ByteBuffer.allocate(bufSize), processor);
	}

	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}
	
	public ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}
	
	public ByteBuffer getProcessedBuffer() {
		return processedBuffer;
	}
		
	public SelectionKey getConnectedPeerKey() {
		return connectedPeerKey;
	}
	
	public void setConnectedPeerKey(SelectionKey connectedPeerKey) {
		this.connectedPeerKey = connectedPeerKey;
	}
	
	public void processRead(SelectionKey key) {
		readBuffer.flip();    // pasa a modo lectura
		processor.process(readBuffer, processedBuffer, key);
		readBuffer.compact();  // pasa a modo escritura
	}

	public void processWrite(SelectionKey key) {
		writeBuffer.flip();
		processor.processWrite(writeBuffer, key);
		writeBuffer.compact();
	}
}
