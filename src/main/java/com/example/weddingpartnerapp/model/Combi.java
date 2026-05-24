package com.example.weddingpartnerapp.model;
public class Combi<T> {
	private String key;
	private T data;
	public Combi() {}
	
	public Combi(String key, T data) {
		super();
		this.key = key;
		this.data = data;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
}
