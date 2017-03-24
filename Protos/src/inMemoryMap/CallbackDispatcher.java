package inMemoryMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

import codecs.Decoder;

public class CallbackDispatcher<T> implements Runnable {
	private final static int BUFFER_CAPACITY = 4096;
	
	private BufferedReader reader;
	private BlockingDeque<Consumer<T>> callbackDeque;
	private CharBuffer buffer;
	private Decoder<T> decoder;
	
	public CallbackDispatcher(InputStream inputStream, Charset charset, Decoder<T> decoder) throws UnsupportedEncodingException {
		reader = new BufferedReader(new InputStreamReader(inputStream, charset));
		callbackDeque = new LinkedBlockingDeque<>();
		buffer = CharBuffer.allocate(BUFFER_CAPACITY);
		this.decoder = decoder;
	}
	
	@Override
	public void run() {
		Consumer<T> callback;
		int read = 0;
		
		buffer.flip();

		try {
			while ((callback = callbackDeque.take()) != NullCallback.getInstance() && read != -1) {		
				if (!buffer.hasRemaining()) {
					buffer.compact();
					read = reader.read(buffer);
					buffer.flip();
				}
				
				if (buffer.hasRemaining()) {
					boolean decoded = decoder.decode(buffer, callback);
					if (!decoded)
						callbackDeque.push(callback);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		callbackDeque.clear();
	}
	
	// TODO: verificar que se llamen cuando este en run()
	
	public void dispatch(Consumer<T> callback) {
		if (callback != null)
			try {
				callbackDeque.put(callback);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@SuppressWarnings("unchecked")
	public void end() {
		try {
			callbackDeque.put((Consumer<T>) NullCallback.getInstance());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}