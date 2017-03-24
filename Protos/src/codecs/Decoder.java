package codecs;

import java.nio.CharBuffer;
import java.util.function.Consumer;

public interface Decoder<T> {
	/**
	 * Decodes characters stored in buffer. Buffer is in read mode. The Decoder class
	 * should not reset de Buffer, meaning call compact or clear. Responsibility is left
	 * to the owner of the Buffer.<p>
	 * The callback is executed if the decode has been done. Further calls to decode may be 
	 * necessary to decode.
	 * @param buffer contains the characters to decode
	 * @return true if value has been decoded, false otherwise.
	 */
	public boolean decode(CharBuffer buffer, Consumer<T> callback);
}
