package socks;

public enum ServerReply {
	SUCCEDED (0x00),
	GENERAL_FAILURE (0x01),
	NOT_ALLOWED (0x02),
	UNREACHABLE (0x03),
	REFUSED (0x04),
	EXPIRED (0x05),
	UNSUPPORTED_CMD (0x06),
	UNSUPPORTED_ADDR (0x08);
	
	public static ServerReply getByCode(byte code) throws IllegalArgumentException {
		for (ServerReply method : values())
			if (method.code() == code)
				return method;
		
		throw new IllegalArgumentException("Unknown command " + code);
	}
	
	private byte code;
	
	private ServerReply(int code) {
		this.code = (byte) code;
	}
	
	public byte code() {
		return code;
	}
}
