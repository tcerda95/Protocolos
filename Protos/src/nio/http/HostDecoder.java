package nio.http;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HostDecoder {

	public String decode(ByteBuffer buffer) {
		while (buffer.get() != 'H' || buffer.get(buffer.position()) != 'o' || buffer.get(buffer.position()+1) != 's' 
				|| buffer.get(buffer.position()+2) != 't' || buffer.get(buffer.position()+3) != ':' || buffer.get(buffer.position()+4) != ' ')
			;
		
		buffer.position(buffer.position()+5);
		
		int beginningPos = buffer.position();
		
		while (buffer.get() != '\n')
			;
		
		int endPos = buffer.position() - 3;
		int length = endPos - beginningPos + 1;
		
		return new String (buffer.array(), beginningPos, length, Charset.forName("ASCII"));

	}
}
