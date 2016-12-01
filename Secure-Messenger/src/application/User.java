package application;

public class User {

	
	@Override
	public String toString() {
		return "User [userName=" + userName + "]";
	}

	private String userName;

	public User(){
		
	}
	
	public User(String userName) {
		super();
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
