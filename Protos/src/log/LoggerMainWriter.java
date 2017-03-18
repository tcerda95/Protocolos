package log;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import interfaces.Log;

public class LoggerMainWriter {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		Log log = new Logger("src/log/log.txt", new MyCodingMethod());
		
		log.warning("loggeando mensaje warning");
		log.info("loggeando mensaje info");
	}

}
