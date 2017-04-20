package nio.http.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class Pruebita {
	public void run(int port) {
		ServerSocketChannel serverChannel;
		Selector selector;
		
		HttpProxySelectorProtocol protocol = new HttpProxySelectorProtocol(4096);
		
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(port));
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		while (true) {
			try {
				selector.select();
			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next();
				keyIter.remove();
				
				if (key.isAcceptable())
					protocol.handleAccept(key);
				
				if (key.isReadable())
					protocol.handleRead(key);
				
				if (key.isConnectable()) {
					
				}
				
				if ()
			}
		}
	}
}
