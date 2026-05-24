package com.example.weddingpartnerapp.model;

import java.io.Serializable;

import jakarta.validation.constraints.*;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer userId;
	
	@NotBlank(message = "{notblank}")
	private String userName;
	
	@Email(message = "{mailaddress}")
	private String mailAddress;
	
	//ハッシュ化されているため、いずれx～y字以内に改正予定
	@NotBlank(message = "{notblank}")
	private String password;
	private String role;
	private byte[] salt;
	
	public User() {}
	
	public User(Integer userId, @NotBlank(message = "{notblank}") String userName,
			@Email(message = "{mailaddress}") String mailAddress,
			@Size(min = 8, max = 16, message = "{password}") String password, String role, byte[] salt) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.mailAddress = mailAddress;
		this.password = password;
		this.role = role;
		this.salt = salt;
	}
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
