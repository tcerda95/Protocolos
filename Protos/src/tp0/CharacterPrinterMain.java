package tp0;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class CharacterPrinterMain {

	public static void main(String[] args) throws IOException {
		String str = "Soy Tomás Cerdá";
		byte[] UTF8bytes = str.getBytes("UTF-8");
		byte[] pizzaUnicodeBytes = {(byte) 0xF0, (byte) 0x9F, (byte) 0x8D, (byte) 0x95};  // UTF-8 encoded
		
		ByteBuffer buffer = ByteBuffer.allocate(UTF8bytes.length + 4);
		
		buffer.put(UTF8bytes);
		buffer.put(pizzaUnicodeBytes);
		
		InputStream input = new ByteArrayInputStream(buffer.array());
		new CharacterPrinter(input).print();
	}

}
