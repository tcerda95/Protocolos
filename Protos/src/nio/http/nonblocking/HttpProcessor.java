package nio.http.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public interface HttpProcessor {
	/**
	 * Reads data from inputBuffer, processes it into outpuBuffer and executes SelectionKey operations
	 * based on the info processed.
	 * @param inputBuffer - Buffer with info to be processed. Should be in read mode.
	 * @param outputBuffer - Buffer to store processed info. Should be in write mode.
	 * @param selectionKey - SelectionKey associated with the connection.
	 */
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey selectionKey);

	public void processWrite(ByteBuffer writeBuffer, SelectionKey key);
}