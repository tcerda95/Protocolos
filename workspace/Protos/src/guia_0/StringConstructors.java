package guia_0;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringConstructors {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(Charset.defaultCharset());  // UTF-8
		System.out.println("a".getBytes().length);	   // 1
		System.out.println("ñ".getBytes().length);	   // 2

		String str = "Soy Tomás Cerdá";
		byte[] strBytesUTF8 = str.getBytes();	// UTF-8 pues es el default charset
		byte[] strBytesUTF16 = str.getBytes("UTF-16LE"); // UTF-16 Little Endian
		
		System.out.println(str.length());
		System.out.println(strBytesUTF8.length);  // las á requieren de dos bytes para codificarse
		System.out.println(strBytesUTF16.length); // cada caracter requiere de 2 bytes para codificarse
		
		System.out.println(new String(strBytesUTF8));
		System.out.println(new String(strBytesUTF8, "UTF-16LE"));
		System.out.println(new String(strBytesUTF16));
		System.out.println(new String(strBytesUTF16, "UTF-16LE"));
		
		System.out.println(new String(Character.toChars(0x1F355)));
	}
}
