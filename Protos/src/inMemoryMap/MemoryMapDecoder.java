package inMemoryMap;

import java.nio.CharBuffer;
import java.util.function.Consumer;

import codecs.Decoder;

public class MemoryMapDecoder implements Decoder<Integer> {
	
	private int value = 0;
	private int sign = 1;
	
	@Override
	public boolean decode(CharBuffer buffer, Consumer<Integer> callback) {
		char c = ' ';

		while (buffer.hasRemaining() && (c = buffer.get()) != '\n') {
			if (c == '-')
				sign = -1;
			else {
				value *= 10;
				value += (int) c - (int) '0';
			}
		}
		
		if (c == '\n') {
			callback.accept(value * sign);
			resetParams();
		}
		
		return c == '\n';
	}

	private void resetParams() {
		value = 0;
		sign = 1;		
	}

}
