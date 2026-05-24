package com.example.weddingpartnerapp.model;
public class BrideDress {
	private Integer brideDressId;
	private Integer brideDressAmount;
	public BrideDress(Integer brideDressId, Integer brideDressAmount) {
		super();
		this.brideDressId = brideDressId;
		this.brideDressAmount = brideDressAmount;
	}
	public Integer getBrideDressId() {
		return brideDressId;
	}
	public void setBrideDressId(Integer brideDressId) {
		this.brideDressId = brideDressId;
	}
	public Integer getBrideDressAmount() {
		return brideDressAmount;
	}
	public void setBrideDressAmount(Integer brideDressAmount) {
		this.brideDressAmount = brideDressAmount;
	}
	
}
