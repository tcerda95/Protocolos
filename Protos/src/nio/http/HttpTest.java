package nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import nio.NioConnectionHandler;
import nio.NioPoolThreadedServer;

public class HttpTest implements NioConnectionHandler {

	@Override
	public void handle(SocketChannel channel) {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		byte[] backingBuffer = buffer.array();
		try {
			while (channel.read(buffer) != -1) {
				buffer.flip();
				System.out.print(new String(backingBuffer, buffer.position(), buffer.remaining(), Charset.forName("ASCII")));
				buffer.rewind();
				analyze(buffer);
				buffer.flip();
				channel.write(buffer);
				buffer.clear();
			}
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void analyze(ByteBuffer buffer) {
		ByteBuffer request = ByteBuffer.allocate(4096);
		if (buffer.get() != (byte) 'G')
			return;
		
		while (buffer.get() != (byte) ' ')
			;
		
		int beginningPos = buffer.position();
		
		while (buffer.get() != (byte) ' ')
			;
		
		int endingPos = buffer.position()-2;
		
		String url = new String(buffer.array(), beginningPos, endingPos-beginningPos+1, Charset.forName("ASCII"));
		URI uri;
		
		while (buffer.get() != (byte) '-' && buffer.get(buffer.position()) != (byte) 'C')
			;
		
		try {
			uri = new URI(url);
			request.put(("GET " + uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : "") + " HTTP/1.1\r\n" + "Host: " + uri.getHost() + "\r\n").getBytes(Charset.forName("ASCII")));
			request.put(buffer);
			
			request.flip();
			System.out.print(new String(request.array(), 0, request.remaining(), Charset.forName("ASCII")));
			
			SocketChannel channel = SocketChannel.open(new InetSocketAddress(uri.getHost(), 80));
			System.out.println(((InetSocketAddress)channel.getRemoteAddress()).getHostString());
			channel.write(request);
			buffer.clear();
			channel.read(buffer);
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws IOException {
		new NioPoolThreadedServer(9998, new HttpHandlerDispatcher()).run();
	}
}
