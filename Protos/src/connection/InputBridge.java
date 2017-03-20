package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Dado un InputStream y un OutputStream, se queda leyendo del InputStream y escribe
 * en el OutputStream.
 */
public class InputBridge implements Runnable {
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	
	private InputStream input;
	private OutputStream output;
	private byte[] buffer;	
	
	public InputBridge(InputStream input, OutputStream output) {
		this(input, output, DEFAULT_BUFFER_SIZE);
	}
	
	public InputBridge(InputStream input, OutputStream output, int bufferSize) {
		if (bufferSize <= 0)
			bufferSize = DEFAULT_BUFFER_SIZE;
		
		this.input = input;
		this.output = output;
		buffer = new byte[bufferSize];
	}
	
	/**
	 * Escucha del InputStream y escribe al OutputStream hasta leer -1.
	 */
	public void run() {
		try {
			tryRun();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tryRun() throws IOException {
		int bytesRead = 0;
		while (bytesRead != -1) {
			bytesRead = input.read(buffer, 0, buffer.length);
			if (bytesRead != -1)
				output.write(buffer, 0, bytesRead);
		}		
	}
}
