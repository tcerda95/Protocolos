package guia_0;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class LoggerMainWriter {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		Log log = new Logger("src/guia_0/log.txt", new MyCodingMethod());
		
		log.warning("loggeando mensaje warning");
		log.info("loggeando mensaje info");
	}

}
