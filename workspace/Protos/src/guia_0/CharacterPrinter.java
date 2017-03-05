package guia_0;

import java.io.IOException;
import java.io.InputStream;

/**
 * Little-Endian, UTF-8 encoded InputStream printer
 */
public class CharacterPrinter {
	private static final byte BYTES_TO_READ_MASK = 0b01000000;
	private static final byte DELETE_MULTI_BYTE_HEADER_MASK = 0b01111111;
	private static final byte DELETE_CONTINUATION_BYTES_MASK = 0b00111111;
	private static final int NON_CONTINUATION_BITS = 6;
	
	private InputStream input;
	
	public CharacterPrinter(InputStream input) {
		this.input = input;
	}
	
	public void print() throws IOException {
		byte b;
		
		while ((b = (byte) input.read()) != -1) {
			int codePoint;
			
			if (isMultiByte(b))
				codePoint = multiByteCodePoint(b);
			else
				codePoint = b;
			
			System.out.print(new String(Character.toChars(codePoint)));
		}
	}

	private boolean isMultiByte(byte b) {
		return b < 0; // leftmost bit is 1 for multi-bytes hence a negative number
	}
	
	private int multiByteCodePoint(byte b) throws IOException {
		int bytesToRead = countBytes(b);
		int codePoint = extractHeader(b, bytesToRead);
		
		for (int i = 0; i < bytesToRead; i++) {
			b = (byte) input.read();
			b &= DELETE_CONTINUATION_BYTES_MASK;
			codePoint <<= NON_CONTINUATION_BITS;
			codePoint += b;
		}
		
		return codePoint;
	}

	private int countBytes(byte b) {
		int n = 0;
		byte mask = BYTES_TO_READ_MASK;
		
		while ((mask & b) != 0) {
			n += 1;
			mask >>= 1;
		}
		
		return n;
	}
	
	private int extractHeader(byte b, int bytesToRead) {
		byte mask = (byte) (DELETE_MULTI_BYTE_HEADER_MASK >> bytesToRead);
		return b & mask;
	}
}
