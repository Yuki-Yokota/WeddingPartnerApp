package com.example.weddingpartnerapp.model;

public class SessionUser {
	private Integer userId;
	private String role;
	
	public SessionUser() {}
	
	public SessionUser(Integer userId, String role) {
		super();
		this.userId = userId;
		this.role = role;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
