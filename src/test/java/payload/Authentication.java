package payload;

public class Authentication {
	int restaurantId;
	int posPin;
	String deviceId;
	boolean logoutNeeded;
	String username;
	String password;
	
	public void setRestaurantId(int restaurantId) {
		this.restaurantId=restaurantId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId=deviceId;
	}
	
	public void setPosPin(int posPin) {
		this.posPin=posPin;
	}
	
	public int getRestaurantId() {
		return restaurantId;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPosPin() {
		return posPin;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setLogoutNeeded(boolean logoutNeeded) {
		this.logoutNeeded=logoutNeeded;
	}
	
	public boolean getLogoutNeeded() {
		return logoutNeeded;
	}

}
