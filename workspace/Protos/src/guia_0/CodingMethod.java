package guia_0;

import guia_0.Log.Message;

public interface CodingMethod {
	public String encode(Log.Message message);
	public Message decode(String str);
	public String getCharsetName();
	public char getSeparator();
}
