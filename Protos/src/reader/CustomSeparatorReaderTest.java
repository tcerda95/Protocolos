package reader;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class CustomSeparatorReaderTest {
	
	@Test
	public void testUntilEndingWithoutSeparator() throws IOException {
		testEquality("hola|como|te|va", '|', "\\|", false);
	}
	
	@Test
	public void testUntilEndingWithSeparator() throws IOException {
		testEquality("hola|como|te|va|", '|', "\\|", false);
	}

	@Test
	public void testIncludeEndingWithoutSeparator() throws IOException {
		testEquality("hola|como|te|va", '|', "\\|", true);
	}
	
	@Test
	public void testIncludeEndingWithSeparator() throws IOException {
		testEquality("hola|como|te|va|", '|', "\\|", true);
	}
	
	private void testEquality(String str, char separator, String separatorStr, boolean appendSep) throws IOException {
		String[] splitted = str.split(separatorStr);
		CustomSeparatorReader reader = buildReader(str, separator);
		
		for (String s : splitted) {
			if (appendSep)
				assertEquals(s.concat(new Character(separator).toString()), reader.readIncludeSeparator());
			else
				assertEquals(s, reader.readUntilSeparator());
		}
	}

	private CustomSeparatorReader buildReader(String str, char separator) {
		return new CustomSeparatorReader(new ByteArrayInputStream(str.getBytes()), separator);
	}
}
