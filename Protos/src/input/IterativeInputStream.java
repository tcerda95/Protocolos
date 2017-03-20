package input;

import java.io.IOException;
import java.io.InputStream;

public class IterativeInputStream extends InputStream {

	private InputStream input;
	
	public IterativeInputStream(InputStream input) {
		this.input = input;
	}
	
	@Override
	public int read() throws IOException {
		return input.read();
	}
	
	@Override
	/**
	 * Read method that ensures len bytes will be read. Therefore it always returns len, unless EOF has been reached.
	 * @param b buffer
	 * @param off offset
	 * @param len length of bytes to be read
	 * @return number of bytes read.
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		if (b.length - off < len)
			throw new IllegalArgumentException("Buffer isn't big enough. Need " + len + " space from " 
		+ off + " to " + b.length + " but have " + (b.length - off) + "space");
		
		int totalBytesRead = 0;
		int read = 0;
		
		while (totalBytesRead < len && read != -1) {
			read = super.read(b, off + totalBytesRead, len - totalBytesRead);
			if (read != -1)
				totalBytesRead += read;
		}
		
		return totalBytesRead > 0 ? totalBytesRead : -1;
	}
}
