package log;

import java.io.FileInputStream;
import java.io.IOException;

import log.Log.Message;

public class LoggerMainReader {

	public static void main(String[] args) throws IOException {
		Log logReader = new Logger("src/guia_0/log/log.txt", new MyCodingMethod());
		
		logReader.read(new FileInputStream("log.txt"), (Message msg) -> System.out.println(msg));
	}

}
