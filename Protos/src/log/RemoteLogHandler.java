package log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.Socket;
import java.util.function.Consumer;

import interfaces.CodingMethod;
import interfaces.ConnectionHandler;
import interfaces.Log;
import interfaces.Log.Message;
import reader.CustomSeparatorReader;
import writer.SimpleBufferedWriter;

public class RemoteLogHandler implements ConnectionHandler {
	private static final String READ_REQUEST = "READ";
	private static final String WRITE_REQUEST = "WRITE";
	private static final String END_READ = "END";
	
	private CodingMethod codingMethod;

	public RemoteLogHandler(CodingMethod codingMethod) {
		this.codingMethod = codingMethod;
	}
	
	@Override
	public void handle(Socket socket) throws IOException {
		CustomSeparatorReader input = new CustomSeparatorReader(socket.getInputStream(), 
				codingMethod.getCharsetName(), codingMethod.getSeparator());
		
		String header = input.readUntilSeparator();		
		String request = extractRequest(header);
		String filename = extractFilename(header);
		
		Log logger = new Logger(filename, codingMethod);
		
		if (request.equals(READ_REQUEST))
			executeRead(logger, filename, socket.getOutputStream());
		else if (request.equals(WRITE_REQUEST))
			executeWrite(logger, input);
		
		socket.close();
	}

	private String extractRequest(String header) {
		return header.split(" ")[0];
	}

	private String extractFilename(String header) {
		return header.split(" ")[1];
	}
	
	private void executeRead(Log logger, String filename, OutputStream output) throws IOException {		
		InputStream fileInput;
		fileInput = new FileInputStream(filename);
		@SuppressWarnings("resource")
		Writer writer = new SimpleBufferedWriter(output, codingMethod.getCharsetName());
		
		logger.read(fileInput, new Consumer<Message>() {

			@Override
			public void accept(Message msg) {
				try {
					writer.write(codingMethod.encode(msg));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		});
		
		writer.write(END_READ + codingMethod.getSeparator());
		writer.flush();
	}
	
	private void executeWrite(Log logger, CustomSeparatorReader input) throws IOException {
		String line;
		
		while ((line = input.readIncludeSeparator()).length() > 0) {
			Message msg = codingMethod.decode(line);
			logger.log(msg);
		}		
	}
}
