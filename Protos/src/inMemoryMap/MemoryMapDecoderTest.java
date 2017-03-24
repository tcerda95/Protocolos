package inMemoryMap;

import static org.junit.Assert.*;

import java.nio.CharBuffer;

import org.junit.Before;
import org.junit.Test;

public class MemoryMapDecoderTest {

	private CharBuffer buffer;
	private MemoryMapDecoder decoder;
	
	@Before
	public void setUp() throws Exception {
		buffer = CharBuffer.allocate(256);
		decoder = new MemoryMapDecoder();
	}

	@Test
	public void test() {
		char[] array = {'-', '2', '5', '0', '9', '1'};
		buffer.put(array);
		
		assertFalse(decoder.decode((CharBuffer) buffer.flip(), null));
		assertFalse(decoder.decode((CharBuffer) buffer, null));

		buffer.compact();
		buffer.put('0');
		
		assertFalse(decoder.decode((CharBuffer) buffer.flip(), null));

		buffer.compact();
		buffer.put('\n').put('a').put('b');
		
		int[] holder = new int[1];
		
		assertTrue(decoder.decode((CharBuffer) buffer.flip(), (ans) -> holder[0] = ans));
		assertEquals(-250910, holder[0]);
		assertTrue(buffer.hasRemaining());
		assertTrue(buffer.remaining() == 2);
	}

}
