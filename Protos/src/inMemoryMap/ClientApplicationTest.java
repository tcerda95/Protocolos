package inMemoryMap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import org.junit.BeforeClass;
import org.junit.Test;

import connection.tcp.ThreadedPoolSocketServer;

public class ClientApplicationTest {

	private static InetAddress address;
	private static Consumer<Integer> invalidConsumer;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		invalidConsumer = (value) -> assertEquals(new Integer(Integer.MIN_VALUE), value);
	}
	
	@Test
	public void uniqueClientTest() throws IOException {
		setUpServer(10000);
		ClientApplication app = new ClientApplication(address, 10000);
		
		app.get("any", invalidConsumer);
		app.set("key", 900, (value) -> assertEquals(new Integer(900), value));
		app.get("key", (value) -> assertEquals(new Integer(900), value));
		app.increment("key", 100, (value) -> assertEquals(new Integer(1000), value));
		app.get("key", (value) -> assertEquals(new Integer(1000), value));
		app.decrement("key", 200, (value) -> assertEquals(new Integer(800), value));
		app.get("key", (value) -> assertEquals(new Integer(800), value));
		
		app.set("key", -20, (value) -> assertEquals(new Integer(-20), value));
		app.get("key", (value) -> assertEquals(new Integer(-20), value));
		
		app.disconnect();
		
		assertFalse(app.isConnected());
	}
	
	private void setUpServer(int port) {		
		try {
			new Thread(new ThreadedPoolSocketServer(address, port, new ServerMemoryMapHandler())).run();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
