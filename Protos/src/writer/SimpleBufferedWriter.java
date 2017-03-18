package writer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class SimpleBufferedWriter extends BufferedWriter {
	public SimpleBufferedWriter(OutputStream outputStream, String charsetName) throws UnsupportedEncodingException, FileNotFoundException {
		super(new OutputStreamWriter(outputStream, charsetName));
	}
}
