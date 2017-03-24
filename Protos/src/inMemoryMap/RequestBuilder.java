package inMemoryMap;

public class RequestBuilder {
	private static final int INITIAL_CAPACITY = 64;
	private static final String SEPARATOR = " ";
	private static final String END = "\n";
	
	private StringBuilder strBuilder = new StringBuilder(INITIAL_CAPACITY);
	
	public String buildRequest(Operation operation, String key) {
		buildOpAndKey(operation, key);
		return strBuilder.append(END).toString();
	}
	
	public String buildRequest(Operation operation, String key, int value) {
		buildOpAndKey(operation, key);
		return strBuilder.append(SEPARATOR).append(value).append(END).toString();
	}
	
	private void buildOpAndKey(Operation operation, String key) {
		strBuilder.setLength(0); // clear builder
		strBuilder.append(operation).append(SEPARATOR).append(key);
	}
}
