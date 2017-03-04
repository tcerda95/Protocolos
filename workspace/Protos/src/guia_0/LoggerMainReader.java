package guia_0;

import java.io.FileInputStream;
import java.io.IOException;

import guia_0.Log.Message;

public class LoggerMainReader {

	public static void main(String[] args) throws IOException {
		Log logReader = new Logger("log.txt", new MyCodingMethod());
		
		logReader.read(new FileInputStream("log.txt"), (Message msg) -> System.out.println(msg));
	}

}
