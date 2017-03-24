package inMemoryMap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class ClientApplication implements ClientMemoryMap {
	private final static Charset CHARSET = Charset.forName("ASCII");
	
	private RequestDispatcher requestDispatcher;
	private CallbackDispatcher<Integer> callbackDispatcher;
	private RequestBuilder requestBuilder;
	private Socket socket;
	private Thread[] threads;
	
	public ClientApplication() throws IOException {
		requestBuilder = new RequestBuilder();
		threads = new Thread[2];
	}
	
	public ClientApplication(InetAddress address, int port) throws IOException {
		this();
		connect(address, port);
	}
	
	@Override
	public void increment(String key, int quantity, Consumer<Integer> callback) {
		assertQuantity(quantity);
		sendSumOperation(key, quantity, callback);
	}

	@Override
	public void decrement(String key, int quantity, Consumer<Integer> callback) {
		assertQuantity(quantity);
		sendSumOperation(key, quantity * -1, callback);
	}
	
	private void sendSumOperation(String key, int quantity, Consumer<Integer> callback) {
		assertConnected();
		String request = requestBuilder.buildRequest(Operation.SUM, key, quantity);
		dispatch(request, callback);
	}

	@Override
	public void get(String key, Consumer<Integer> callback) {
		assertConnected();
		String request = requestBuilder.buildRequest(Operation.GET, key);
		dispatch(request, callback);
	}

	@Override
	public void set(String key, int value, Consumer<Integer> callback) {
		assertConnected();
		String request = requestBuilder.buildRequest(Operation.SET, key, value);
		dispatch(request, callback);
	}

	private void dispatch(String request, Consumer<Integer> callback) {
		requestDispatcher.dispatch(request);
		callbackDispatcher.dispatch(callback);
	}
	
	@Override
	public void connect(InetAddress address, int port) throws IOException {
		if (isConnected())
			throw new IllegalStateException("Can't connect: already connected to " 
		+ socket.getInetAddress() + ":" + socket.getPort());
		
		socket = new Socket(address, port);
		
		requestDispatcher = new RequestDispatcher(socket.getOutputStream(), CHARSET);
		callbackDispatcher = new CallbackDispatcher<Integer>(socket.getInputStream(), CHARSET, new MemoryMapDecoder());
		
		threads[0] = new Thread(requestDispatcher);
		threads[1] = new Thread(callbackDispatcher);
		
		threads[0].start();
		threads[1].start();
	}

	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	@Override
	public void disconnect() throws IOException {
		if (isConnected()) {
			
			try {
				requestDispatcher.end();
				callbackDispatcher.end();

				threads[0].join();
				threads[1].join();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			socket.close();
			socket = null;
		}
	}
	
	private void assertQuantity(int quantity) {
		if (quantity < 0)
			throw new IllegalArgumentException("Quantity must be >= 0 but received quantity = " + quantity);
	}
	
	private void assertConnected() {
		if (!isConnected())
			throw new IllegalStateException("Must be connected in order to send a request");
	}
}
