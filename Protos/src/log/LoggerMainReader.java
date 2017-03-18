package log;

import java.io.FileInputStream;
import java.io.IOException;

import interfaces.Log;
import interfaces.Log.Message;

public class LoggerMainReader {

	public static void main(String[] args) throws IOException {
		Log logReader = new Logger("src/log/log.txt", new MyCodingMethod());
		
		logReader.read(new FileInputStream("src/log/log.txt"), (Message msg) -> System.out.println(msg));
	}

}
