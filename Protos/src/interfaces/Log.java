package interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.function.Consumer;

/** Registra mensajes */
public interface Log {
	/** registra un mensaje con el nivel warning */
	public void warning(String msg);
	
	/** registra un mensaje con el nivel info */
	public void info(String msg);
	
	/** registra un mensaje */
	public void log(Message msg);
	
	/** dado un input stream consume uno a uno los mensajes 
	 * @throws IOException */
	public void read(InputStream input, final Consumer<Message> consumer) throws IOException;
	
	public enum Level {
		warning,
		info
	}
	
	public interface Message {
		public Date when();
		public String getMessage();
		public Level getLevel();
	}
}
