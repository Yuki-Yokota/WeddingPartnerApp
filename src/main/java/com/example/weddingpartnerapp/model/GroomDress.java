package com.example.weddingpartnerapp.model;
public class GroomDress {
	private Integer groomDressId;
	private Integer groomDressAmount;
	public GroomDress(Integer groomDressId, Integer groomDressAmount) {
		super();
		this.groomDressId = groomDressId;
		this.groomDressAmount = groomDressAmount;
	}
	public Integer getGroomDressId() {
		return groomDressId;
	}
	public void setGroomDressId(Integer groomDressId) {
		this.groomDressId = groomDressId;
	}
	public Integer getGroomDressAmount() {
		return groomDressAmount;
	}
	public void setGroomDressAmount(Integer groomDressAmount) {
		this.groomDressAmount = groomDressAmount;
	}
	
}
