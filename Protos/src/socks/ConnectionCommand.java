package socks;

public enum ConnectionCommand {
	CONNECT (0x01),
	BIND (0x02),
	UDP (0x03);
		
	public static ConnectionCommand getByCode(byte code) throws IllegalArgumentException {
		for (ConnectionCommand method : values())
			if (method.code() == code)
				return method;
		
		throw new IllegalArgumentException("Unknown command " + code);
	}
	
	private byte code;
	
	private ConnectionCommand(int code) {
		this.code = (byte) code;
	}
	
	public byte code() {
		return code;
	}
}
