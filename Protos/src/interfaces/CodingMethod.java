package interfaces;

import interfaces.Log.Message;

public interface CodingMethod {
	public String encode(Log.Message message);
	public Message decode(String str);
	public String getCharsetName();
	public char getSeparator();
}
