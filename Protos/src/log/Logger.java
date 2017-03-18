package log;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import interfaces.CodingMethod;
import interfaces.Log;
import reader.CustomSeparatorReader;
import writer.SimpleBufferedWriter;

public class Logger implements Log {	
	private BufferedWriter outputWriter;
	private CodingMethod codingMethod;

	public Logger(OutputStream outputStream, CodingMethod codingMethod) throws UnsupportedEncodingException, FileNotFoundException {
		this.outputWriter = new SimpleBufferedWriter(outputStream, codingMethod.getCharsetName());
		this.codingMethod = codingMethod;
	}
	
	public Logger(String filename, CodingMethod codingMethod) throws UnsupportedEncodingException, FileNotFoundException {
		this(new FileOutputStream(filename, true), codingMethod);
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
	
	@Override
	public void log(Message msg) {
		write(msg);
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
		CustomSeparatorReader inputReader = buildCustomSeparatorReader(input);
		String line;
		
		while ((line = inputReader.readIncludeSeparator()).length() > 0)
			consumer.accept(codingMethod.decode(line));
	}
	
	private CustomSeparatorReader buildCustomSeparatorReader(InputStream input) {
		try {
			return new CustomSeparatorReader(input, codingMethod.getCharsetName(), codingMethod.getSeparator());
		}
		catch (UnsupportedEncodingException e) {
			System.err.println("Failed to use given charset.\nUsing default charset");
			return new CustomSeparatorReader(input, codingMethod.getSeparator());
		}
	}
}
