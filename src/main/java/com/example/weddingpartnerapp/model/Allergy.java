package com.example.weddingpartnerapp.model;
public class Allergy {
	private Integer allergyId;
	private String allergyName;
	public Allergy(Integer allergyId, String allergyName) {
		super();
		this.allergyId = allergyId;
		this.allergyName = allergyName;
	}
	public Allergy() {
		}
	public Integer getAllergyId() {
		return allergyId;
	}
	public void setAllergyId(Integer allergyId) {
		this.allergyId = allergyId;
	}
	public String getAllergyName() {
		return allergyName;
	}
	public void setAllergyName(String allergyName) {
		this.allergyName = allergyName;
	}
	
}
