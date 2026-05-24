package com.example.weddingpartnerapp.model;
public class Dish {
	private Integer dishId;
	private Integer dishAmount;
	public Dish(Integer dishId, Integer dishAmount) {
		super();
		this.dishId = dishId;
		this.dishAmount = dishAmount;
	}
	public Integer getDishId() {
		return dishId;
	}
	public void setDishId(Integer dishId) {
		this.dishId = dishId;
	}
	public Integer getDishAmount() {
		return dishAmount;
	}
	public void setDishAmount(Integer dishAmount) {
		this.dishAmount = dishAmount;
	}
	
}
