package model;

public class Log {
	private String date;
	private String action;
	public Log(String date, String action) {
		super();
		this.date = date;
		this.action = action;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
}
