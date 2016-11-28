package application;

public class Message {
	
	private String message;
	private Boolean recepient;
	
	
	
	public Message(String message, Boolean recepient) {
		super();
		this.message = message;
		this.recepient = recepient;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getRecepient() {
		return recepient;
	}
	public void setRecepient(Boolean recepient) {
		this.recepient = recepient;
	}
	
	

}
