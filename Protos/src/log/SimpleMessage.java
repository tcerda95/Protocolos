package log;

import java.util.Date;

import interfaces.Log.Level;
import interfaces.Log.Message;

public class SimpleMessage implements Message {
	
	private Date when;
	private String msg;
	private Level lvl;
	
	public SimpleMessage(Date date, String msg, Level lvl) {
		this.when = date;
		this.msg = msg;
		this.lvl = lvl;
	}
	
	public SimpleMessage(String msg, Level lvl) {
		this(new Date(), msg, lvl);
	}

	@Override
	public Date when() {
		return when;
	}

	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public Level getLevel() {
		return lvl;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		
		appendDate(strBuilder);
		appendLevel(strBuilder);
		appendMsg(strBuilder);
		
		return strBuilder.toString();
	}
	
	private void appendDate(StringBuilder strBuilder) {
		strBuilder.append(when().toString() + " ");		
	}

	private void appendLevel(StringBuilder strBuilder) {
		if (getLevel().equals(Level.warning))
			strBuilder.append("WARNING ");
		else
			strBuilder.append("INFO ");
	}

	private void appendMsg(StringBuilder strBuilder) {
		strBuilder.append(msg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lvl == null) ? 0 : lvl.hashCode());
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		result = prime * result + ((when == null) ? 0 : when.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		SimpleMessage other = (SimpleMessage) obj;
		if (lvl != other.lvl)
			return false;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		if (when == null) {
			if (other.when != null)
				return false;
		} else if (!when.equals(other.when))
			return false;
		return true;
	}
}
