package com.example.weddingpartnerapp.model;
public class WeddingType {
	Integer weddingTypeId;
	String weddingTypeName;
	Integer weddingTypeAmount;
	
	public WeddingType() {}
	public WeddingType(Integer weddingTypeId, String weddingTypeName, Integer weddingTypeAmount) {
		super();
		this.weddingTypeId = weddingTypeId;
		this.weddingTypeName = weddingTypeName;
		this.weddingTypeAmount = weddingTypeAmount;
	}
	public Integer getWeddingTypeId() {
		return weddingTypeId;
	}
	public void setWeddingTypeId(Integer weddingTypeId) {
		this.weddingTypeId = weddingTypeId;
	}
	public String getWeddingTypeName() {
		return weddingTypeName;
	}
	public void setWeddingTypeName(String weddingTypeName) {
		this.weddingTypeName = weddingTypeName;
	}
	public Integer getWeddingTypeAmount() {
		return weddingTypeAmount;
	}
	public void setWeddingTypeAmount(Integer weddingTypeAmount) {
		this.weddingTypeAmount = weddingTypeAmount;
	}
	
}
