package inMemoryMap;

import java.util.function.Consumer;

public class NullCallback implements Consumer<Object> {
	private static final NullCallback INSTANCE = new NullCallback();
	
	public static NullCallback getInstance() {
		return INSTANCE;
	}
	
	private NullCallback() {
	}

	@Override
	public void accept(Object t) {		
	}
}
