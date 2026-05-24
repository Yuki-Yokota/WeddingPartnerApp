package com.example.weddingpartnerapp.model;
public class Drink {
	private Integer drinkId;
	private Integer drinkAmount;
	public Drink(Integer drinkId, Integer drinkAmount) {
		super();
		this.drinkId = drinkId;
		this.drinkAmount = drinkAmount;
	}
	public Integer getDrinkId() {
		return drinkId;
	}
	public void setDrinkId(Integer drinkId) {
		this.drinkId = drinkId;
	}
	public Integer getDrinkAmount() {
		return drinkAmount;
	}
	public void setDrinkAmount(Integer drinkAmount) {
		this.drinkAmount = drinkAmount;
	}
	
}
