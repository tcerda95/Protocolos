package inMemoryMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import writer.SimpleBufferedWriter;

// TODO: pensar interfaz dispatcher

public class RequestDispatcher implements Runnable {
	private BufferedWriter output;
	private BlockingQueue<String> requestQueue;
	
	public RequestDispatcher(OutputStream output, Charset charset) throws IOException {
		this.output = new SimpleBufferedWriter(output, charset);
		requestQueue = new LinkedBlockingQueue<>();
	}

	@Override
	public void run() {
		String request;
		
		try {
			
			while ((request = requestQueue.take()).length() > 0) {
				output.write(request);
				output.flush();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		requestQueue.clear();
	}
	
	
	public void dispatch(String request) {
		if (request.length() > 0)
			try {
				requestQueue.put(request);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public void end() {
		try {
			requestQueue.put("");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
