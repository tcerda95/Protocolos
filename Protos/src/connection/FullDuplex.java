package connection;

import java.io.IOException;
import java.net.Socket;

public class FullDuplex {
	private Socket peer1;
	private Socket peer2;
	
	public FullDuplex(Socket peer1, Socket peer2) {
		this.peer1 = peer1;
		this.peer2 = peer2;
	}
	
	public void run() throws IOException, InterruptedException {
		InputBridge bridge1 = new InputBridge(peer1.getInputStream(), peer2.getOutputStream());
		InputBridge bridge2 = new InputBridge(peer2.getInputStream(), peer1.getOutputStream());
		
		Thread t1 = new Thread(bridge1);
		Thread t2 = new Thread(bridge2);
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
	}
	
}
