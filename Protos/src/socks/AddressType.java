package socks;

public enum AddressType {
	IPV4 (0x01),
	DOMAIN_NAME (0x03),
	IPV6 (0x04);
	
	public static AddressType getByCode(byte code) throws IllegalArgumentException {
		for (AddressType method : values())
			if (method.code() == code)
				return method;
		
		throw new IllegalArgumentException("Unknown adres type " + code);
	}
	
	private byte code;
	
	private AddressType(int code) {
		this.code = (byte) code;
	}
	
	public byte code() {
		return code;
	}
}
