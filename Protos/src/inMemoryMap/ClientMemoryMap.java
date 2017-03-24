package inMemoryMap;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Consumer;

public interface ClientMemoryMap {
	public void increment(String key, int quantity, Consumer<Integer> callback);
	public void decrement(String key, int quantity, Consumer<Integer> callback);
	public void set(String key, int value, Consumer<Integer> callback);
	public void get(String key, Consumer<Integer> callback);
	public void connect(InetAddress address, int port) throws IOException;
	public boolean isConnected();
	public void disconnect() throws IOException;
}