package guia_0;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import guia_0.Log.Message;

public class MyCodingMethodTest {

	private MyCodingMethod coder;
	
	@Before
	public void setUp() throws Exception {
		coder = new MyCodingMethod();
	}

	@Test
	public void testInfoEncode() {
		Message infoMsg =  new SimpleMessage(new Date(1000), "test info msg", Log.Level.info);
		String expectedInfoStr = "3E8Itest info msg\n";
		
		assertEquals(coder.encode(infoMsg), expectedInfoStr);
	}
	
	@Test
	public void testWarningEncode() {
		Message warningMsg =  new SimpleMessage(new Date(5000), "test warning msg", Log.Level.warning);
		String expectedWarningStr = "1388Wtest warning msg\n";
		
		assertEquals(coder.encode(warningMsg), expectedWarningStr);
	}

	@Test
	public void testInfoDecode() {
		String codedStr = "1388Itest info coded msg\n";
		Message expectedMsg = new SimpleMessage(new Date(5000), "test info coded msg", Log.Level.info);
				
		assertEquals(coder.decode(codedStr), expectedMsg);
	}

	@Test
	public void testWarningDecode() {
		String codedStr = "3E8Wtest warning coded msg\n";
		Message expectedMsg = new SimpleMessage(new Date(1000), "test warning coded msg", Log.Level.warning);

		assertEquals(coder.decode(codedStr), expectedMsg);
	}
}
