package socks;

public enum AuthorizationMethod {
	NO_AUTHORIZATION (0x00),
	GSSAPI (0x01),
	USER_PASS (0x02),
	NO_ACCEPTABLE (0xFF);
	
	public static AuthorizationMethod getByCode(byte code) {
		for (AuthorizationMethod method : values())
			if (method.code() == code)
				return method;
		return NO_ACCEPTABLE;
	}
	
	private byte code;
	
	private AuthorizationMethod(int code) {
		this.code = (byte) code;
	}
	
	public byte code() {
		return code;
	}
}
