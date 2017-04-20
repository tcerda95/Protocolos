package nio.http.nonblocking;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public interface HttpProcessor {
	public void process(ByteBuffer inputBuffer, ByteBuffer outputBuffer, SelectionKey selectionKey);
}
