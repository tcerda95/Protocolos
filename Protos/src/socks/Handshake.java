package socks;

import java.io.IOException;
import java.io.InputStream;

import input.IterativeInputStream;

// Alternativa más eficiente: constructor default donde se crea buffer.
// decode método público que recibe InputStream.

public class Handshake {	
	private int version;
	private AuthorizationMethod[] methods;
	
	public Handshake(InputStream input) throws IOException {
		decode(new IterativeInputStream(input));
	}

	private void decode(IterativeInputStream input) throws IOException {
		version = input.read();
		int nMethods = input.read();
		
		if (nMethods < 0)
			nMethods = 0;
		
		methods = new AuthorizationMethod[nMethods];
		byte[] buffer = new byte[nMethods]; // Considerar recibir buffer por constructor para no crear uno nuevo siempre

		input.read(buffer);
		
		for (int i = 0; i < nMethods; i++)
			methods[i] = AuthorizationMethod.getByCode(buffer[i]);
	}
	
	public int getVersion() {
		return version;
	}
	
	public AuthorizationMethod[] getMethods() {
		return methods;
	}
	
	public boolean hasMethod(AuthorizationMethod method) {
		for (AuthorizationMethod each : methods)
			if (each == method)
				return true;
		return false;
	}
	
}
