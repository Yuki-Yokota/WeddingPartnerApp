package com.example.weddingpartnerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "プランナーID","新郎名","新婦名", "メールアドレス", "電話番号"})
public class CSVCustomer {
	
	@JsonProperty("ID")
	@NotNull(message = "{notnull}")
	private Integer customerId;
	
	@JsonProperty("プランナーID")
	@NotBlank(message = "{notblank}")
	private Integer userId;
	
	@JsonProperty("新郎名")
	@Size(min = 1, max = 20,message="{len}")
	private String groomName;
	
	@JsonProperty("新婦名")
	@Size(min = 1, max = 20,message="{len}")
	private String brideName;
	
	@JsonProperty("メールアドレス")
	@Email(message = "{mailaddress}")
	private String mailAddress;
	
	@JsonProperty("電話番号")
	@NotBlank(message = "{notblank}")
	private String phoneNumber;

	public CSVCustomer() {}
	
	public CSVCustomer(CustomerDTO customer) {
		super();
		this.customerId = customer.getCustomerId();
		this.userId = customer.getUserId();
		this.groomName = customer.getGroomName();
		this.brideName = customer.getBrideName();
		this.mailAddress = customer.getMailAddress();
		this.phoneNumber = customer.getPhoneNumber();
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
	}
	
	
}
