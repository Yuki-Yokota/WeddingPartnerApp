package com.example.weddingpartnerapp.common;
public enum HomeAction {
	INACTIVE,
	CUSTOMER,
	CONTRACT,
	WEDDING,
	GUEST,
	USER,
	HISTORY;
	private String name;
	private HomeAction(String name) {
		this.name=name;
	}
	HomeAction() {}
	public String getName() {
		return this.name;
	}
	public static HomeAction form(String action) {
		try {
			if(action!=null) {
				return HomeAction.valueOf(action);
			}
		}catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return INACTIVE;
	}
	
}
