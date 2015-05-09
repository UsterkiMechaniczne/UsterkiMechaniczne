package model;

public class Client {

	private String firstName;
	private String lastName;
	private String numberPlate;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getNumberPlate() {
		return numberPlate;
	}
	public void setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Client(String firstName, String lastName, String numberPlate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.numberPlate = numberPlate;
	}
	@Override
	public String toString() {
		return "Client [firstName=" + firstName + ", lastName=" + lastName
				+ ", numberPlate=" + numberPlate + "]";
	}
	
	
}
