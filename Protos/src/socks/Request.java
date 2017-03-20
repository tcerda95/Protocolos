package socks;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import input.IterativeInputStream;

public class Request {
	private static final int BUF_SIZE = 256;
	private static final int HEADER_LEN = 4; // longitud hasta DST.ADDR
	private static final int BASE_REQUEST_LEN = 6; // longitud de la request sin DST.ADDR
	
	private byte[] buffer = new byte[BUF_SIZE];
	private int version;

	private ConnectionCommand command;
	private AddressType addressType;
	private InetAddress destinationAddress;
	private int destinationPort;
	private int addrLen;
	
	public Request(InputStream input) throws IOException {
		decode(new IterativeInputStream(input));
	}

	private void decode(IterativeInputStream input) throws IOException {
		decodeHeader(input);
		decodeAddress(input);
		decodePort(input);
	}

	private void decodeHeader(IterativeInputStream input) throws IOException {
		input.read(buffer, 0, 4);
		
		version = buffer[0];
		command = ConnectionCommand.getByCode(buffer[1]);
		addressType = AddressType.getByCode(buffer[3]);

		if (buffer[2] != 0)
			throw new IllegalStateException("Third byte must be the reserved byte 0x00, but it is " + buffer[2]);
	}
	
	private void decodeAddress(IterativeInputStream input) throws IOException {
		if (addressType == AddressType.IPV4)
			decodeAddressIPv4(input);
		else if (addressType == AddressType.DOMAIN_NAME)
			decodeDomainName(input);
		else
			throw new IllegalStateException("Unsupported address type");
	}
	
	private void decodeAddressIPv4(IterativeInputStream input) throws IOException {
		byte[] ipv4 = new byte[4];
		input.read(ipv4);
		
		for (int i = 0; i < ipv4.length; i++)
			buffer[HEADER_LEN + i] = ipv4[i];
		
		destinationAddress = InetAddress.getByAddress(ipv4);
		addrLen = 4;
	}

	private void decodeDomainName(IterativeInputStream input) throws IOException {
		int len = input.read();
		input.read(buffer, HEADER_LEN, len);
		String domainName = new String(buffer, HEADER_LEN, len, "US-ASCII");
		
		destinationAddress = InetAddress.getByName(domainName);
		addrLen = len+1;
	}
	
	private void decodePort(IterativeInputStream input) throws IOException {
		int offset = HEADER_LEN + addrLen;
		input.read(buffer, offset, 2); // puerto en big endian
		destinationPort = (buffer[offset] & 0xFF) << 8 | (buffer[offset + 1] & 0xFF);
	}
	
	public int getVersion() {
		return version;
	}

	public ConnectionCommand getCommand() {
		return command;
	}

	public InetAddress getDestinationAddress() {
		return destinationAddress;
	}

	public int getDestinationPort() {
		return destinationPort;
	}
	
	public int getLen() {
		return addrLen + BASE_REQUEST_LEN;
	}
	
	public byte[] getRequestBytes() {
		return buffer;
	}
}
