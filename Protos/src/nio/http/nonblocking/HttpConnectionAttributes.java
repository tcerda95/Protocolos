package nio.http.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class HttpConnectionAttributes {	
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private HttpProcessor processor;
	
	public HttpConnectionAttributes(ByteBuffer readBuffer, ByteBuffer writeBuffer, HttpProcessor processor) {
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
		this.processor = processor;
	}
	
	public HttpConnectionAttributes(int bufSize, HttpProcessor processor) {
		this(ByteBuffer.allocate(bufSize), ByteBuffer.allocate(bufSize), processor);
	}

	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}
	
	public ByteBuffer getWriteBuffer() {
		return writeBuffer;
	}
		
	public void processRead(SelectionKey key) {
		readBuffer.flip();    // pasa a modo lectura
		writeBuffer.compact();   // pasa a modo escritura
		processor.process(readBuffer, writeBuffer, key);
		writeBuffer.flip();   // pasa a modo lectura
		readBuffer.compact();  // pasa a modo escritura
	}
}
