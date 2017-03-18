package log;

import java.util.Date;

import interfaces.CodingMethod;
import interfaces.Log.Level;
import interfaces.Log.Message;

public class MyCodingMethod implements CodingMethod {
	public static final String charsetName = "US-ASCII";
	
	private static final char WARNING_LEVEL = 'W';
	private static final char INFO_LEVEL = 'I';
	private static final char SEPARATOR = '\n';

	@Override
	public String getCharsetName() {
		return charsetName;
	}
	
	@Override
	public char getSeparator() {
		return SEPARATOR;
	}
	
	@Override
	public Message decode(String str) {
		char[] charArray = str.toCharArray();
		return decodeMessage(charArray);				
	}

	private Message decodeMessage(char[] charArray) {
		int i = 0;
		
		StringBuilder hexaMilisBuilder = new StringBuilder("0x");
		
		while (charArray[i] != WARNING_LEVEL && charArray[i] != INFO_LEVEL)
			hexaMilisBuilder.append(charArray[i++]);
		
		Date date = new Date(Long.decode(hexaMilisBuilder.toString()));
		
		Level lvl = charArray[i++] == WARNING_LEVEL ? Level.warning : Level.info;
		
		StringBuilder msgBuilder = new StringBuilder();
		
		while (charArray[i] != SEPARATOR)
			msgBuilder.append(charArray[i++]);
				
		return new SimpleMessage(date, msgBuilder.toString(), lvl);
	}

	@Override
	public String encode(Message message) {
		StringBuilder strBuilder = new StringBuilder();
		
		appendWhen(strBuilder, message);
		appendLevel(strBuilder, message);
		appendMessage(strBuilder, message);
		appendSeparator(strBuilder, message);
		
		return strBuilder.toString();
	}
	
	private void appendWhen(StringBuilder strBuilder, Message message) {
		long milliseconds = message.when().getTime();
		strBuilder.append(Long.toHexString(milliseconds).toUpperCase());
	}

	private void appendLevel(StringBuilder strBuilder, Message message) {
		Level lvl = message.getLevel();
		
		if (lvl.equals(Level.info))
			strBuilder.append(INFO_LEVEL);
		else
			strBuilder.append(WARNING_LEVEL);
	}

	private void appendMessage(StringBuilder strBuilder, Message message) {
		strBuilder.append(message.getMessage());
	}
	
	private void appendSeparator(StringBuilder strBuilder, Message message) {
		strBuilder.append(SEPARATOR);
	}
	
}
