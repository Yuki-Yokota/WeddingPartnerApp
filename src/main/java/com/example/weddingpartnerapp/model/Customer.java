package com.example.weddingpartnerapp.model;
public class Customer {
	private Integer customerId;
	private Integer userId;
	private String groomName;
	private String brideName;
	private String mailAddress;
	private String phoneNumber;
	
	public Customer(CustomerDTO cusDto) {
		super();
		this.customerId = cusDto.getCustomerId();
		this.userId = cusDto.getUserId();
		this.groomName = cusDto.getGroomName();
		this.brideName = cusDto.getBrideName();
		this.mailAddress = cusDto.getMailAddress();
		this.phoneNumber = cusDto.getPhoneNumber();
	}
	public Customer() {};
	public String getGroomName() {
		return groomName;
	}
	public void setGroomName(String groomName) {
		this.groomName = groomName;
	}
	public String getBrideName() {
		return brideName;
	}
	public void setBrideName(String brideName) {
		this.brideName = brideName;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
