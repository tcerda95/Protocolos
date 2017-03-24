package inMemoryMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.ConnectionHandler;
import writer.SimpleBufferedWriter;

public class ServerMemoryMapHandler implements ConnectionHandler {
	private final static Charset CHARSET = Charset.forName("ASCII");
	private final static char SEPARATOR = '\n';
	
	private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
	
	@Override
	public void handle(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
		BufferedWriter writer = new SimpleBufferedWriter(socket.getOutputStream(), CHARSET);
		StringBuilder stringBuilder = new StringBuilder();
		String request;
				
		while ((request = reader.readLine()) != null) {
			String[] splitted = request.split(" ");
			int ans = executeRequest(splitted);
			sendResponse(ans, stringBuilder, writer);
		}
				
		reader.close();
		writer.close();
	}
	
	private void sendResponse(int ans, StringBuilder stringBuilder, BufferedWriter writer) throws IOException {
		stringBuilder.append(ans);
		stringBuilder.append(SEPARATOR);

		writer.write(stringBuilder.toString());
		writer.flush();
		
		stringBuilder.setLength(0);
	}

	private int executeRequest(String[] splitted) {
		String key = splitted[1];
		switch (Operation.valueOf(splitted[0])) {
		case GET:
			Integer currentValue = map.get(key);
			return currentValue != null ? currentValue : Integer.MIN_VALUE;
		case SET:
			int value = Integer.parseInt(splitted[2]);
			map.put(key, value);
			return value;
		case SUM:
			int quantity = Integer.parseInt(splitted[2]);
			return map.computeIfPresent(key, (unused, oldValue) -> oldValue + quantity);
		default:
			return Integer.MIN_VALUE;
		}
	}
}
