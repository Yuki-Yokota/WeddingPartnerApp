package com.example.weddingpartnerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerDTO {
	private Integer customerId;
	private Integer userId;
	
	@Size(min = 1, max = 20,message="{len}")
	private String groomName;
	
	@Size(min = 1, max = 20,message="{len}")
	private String brideName;
	
	@Email(message = "{mailaddress}")
	private String mailAddress;
	
	@NotBlank(message = "{notblank}")
	private String phoneNumber;
	
	public CustomerDTO(Customer customer) {
		super();
		this.customerId = customer.getCustomerId();
		this.userId = customer.getUserId();
		this.groomName = customer.getGroomName();
		this.brideName = customer.getBrideName();
		this.mailAddress = customer.getMailAddress();
		this.phoneNumber = customer.getPhoneNumber();
	}
	public CustomerDTO(CSVCustomer customer) {
		super();
		this.customerId = customer.getCustomerId();
		this.userId = customer.getUserId();
		this.groomName = customer.getGroomName();
		this.brideName = customer.getBrideName();
		this.mailAddress = customer.getMailAddress();
		this.phoneNumber = customer.getPhoneNumber();
	}
	public CustomerDTO() {}
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
	};
	
}
