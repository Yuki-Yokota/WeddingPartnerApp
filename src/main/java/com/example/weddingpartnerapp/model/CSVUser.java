package com.example.weddingpartnerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "名前", "メールアドレス", "権限"})

public class CSVUser {
	@JsonProperty("ID")
	@NotNull(message = "{notnull}")
	private Integer userId;
	
	@JsonProperty("名前")
	@NotBlank(message = "{notblank}")
	private String userName;
	
	@JsonProperty("メールアドレス")
	@Email(message = "{mailaddress}")
	private String mailAddress;
	
	@JsonProperty("権限")
	@NotBlank(message = "{notblank}")
	private String role;
	
	public CSVUser(@NotBlank(message = "{notblank}") Integer userId,@NotBlank(message = "{notblank}") String userName,
			@Email(message = "{mailaddress}") String mailAddress,@NotBlank(message = "{notblank}") String role) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.mailAddress = mailAddress;
		this.role = role;
	}
	public CSVUser() {}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
