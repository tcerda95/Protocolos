package guia_0;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

public class Logger implements Log {	
	private BufferedWriter outputWriter;
	private CodingMethod codingMethod;

	public Logger(String filename, CodingMethod codingMethod) throws UnsupportedEncodingException, FileNotFoundException {
		this.outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream (filename, true), codingMethod.getCharsetName()));
		this.codingMethod = codingMethod;
	}
	
	@Override
	public void warning(String msg) {
		Message message = new SimpleMessage(msg, Level.warning);
		write(message);
	}

	@Override
	public void info(String msg) {
		Message message = new SimpleMessage(msg, Level.info);
		write(message);
	}
	
	private void write(Message message) {
		try {
			outputWriter.write(codingMethod.encode(message));
			outputWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(InputStream input, Consumer<Message> consumer) throws IOException {
		InputStreamReader inputReader = buildInputStreamReader(input);
		String line;
		
		while ((line = readLine(inputReader)).length() > 0)
			consumer.accept(codingMethod.decode(line));
	}
	
	private InputStreamReader buildInputStreamReader(InputStream input) {
		try {
			return new InputStreamReader(input, codingMethod.getCharsetName());
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println("Failed to use given charset.\nUsing default charset");
			return new InputStreamReader(input);
		}
	}

	private String readLine(InputStreamReader input) throws IOException {
		StringBuilder strBuilder = new StringBuilder();
		char separatorChar = codingMethod.getSeparator();
		char c;
		
		while ((c = (char) input.read()) != -1 && c != separatorChar) {
			strBuilder.append(String.valueOf(c));
		}
				
		if (c != -1)
			strBuilder.append(c);  // append separator
				
		return strBuilder.toString();
	}
}
