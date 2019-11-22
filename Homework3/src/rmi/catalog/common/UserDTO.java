package rmi.catalog.common;

public class UserDTO {
	private String userName;
	private String userPass;
	
	public UserDTO(String userName, String userPass) {
		this.userName = userName;
		this.userPass= userPass;
	}

	public String getUsername() {
		return this.userName;
	}
	
	public String getPassword() {
		return this.userPass;
	}
	
}
