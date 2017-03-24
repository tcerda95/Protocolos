package reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class CustomSeparatorReader extends BufferedReader {
	char separator;
	
	public CustomSeparatorReader(InputStream input, String charsetName, char separator) throws UnsupportedEncodingException {
		super(new InputStreamReader(input, charsetName));
		this.separator = separator;
	}

	public CustomSeparatorReader(InputStream input, Charset charset, char separator) throws UnsupportedEncodingException {
		this(input, charset.name(), separator);
	}
	
	public CustomSeparatorReader(InputStream input, char separator) {
		super(new InputStreamReader(input));
		this.separator = separator;
	}

	/**
	 * Reads input stream until separator character or end of stream.
	 * @return String until separator character or end of stream. 
	 * Returns a String of length 0 if read when end of file has been reached
	 * @throws IOException
	 */
	public String readUntilSeparator() throws IOException {
		return buildIncludeSeparator(false);
	}
	
	/**
	 * Reads input stream including separator character or end of stream.
	 * @return String including separator character or end of stream. 
	 * Returns a String of length 0 if read when end of file has been reached.
	 * @throws IOException
	 */
	public String readIncludeSeparator() throws IOException {
		return buildIncludeSeparator(true);
	}
	
	private String buildIncludeSeparator(boolean includeSeparator) throws IOException {
		StringBuilder strBuilder = new StringBuilder();
		int c;
		
		while ((c = read()) != -1 && c != separator) {
			strBuilder.append(String.valueOf((char) c));
		}
		
		if (c != -1 && includeSeparator)
			strBuilder.append(separator);
				
		return strBuilder.toString();
	}
}
