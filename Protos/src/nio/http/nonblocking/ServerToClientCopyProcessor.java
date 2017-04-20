package nio.http.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class ServerToClientCopyProcessor implements HttpProcessor {

	@Override
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey selectionKey) {
		outputBuffer.put(inputBuffer);		
	}

}
