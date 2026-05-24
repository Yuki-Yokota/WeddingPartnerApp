package com.example.weddingpartnerapp.model;
public class Venue {
	private Integer venueId;
	private String venueName;
	private String phoneNumber;
	private Integer venueAmount;
	public Venue(Integer venueId, String venueName, String phoneNumber,Integer venueAmount) {
		super();
		this.venueId = venueId;
		this.venueName = venueName;
		this.phoneNumber = phoneNumber;
		this.venueAmount = venueAmount;
	}
	public Venue() {}
	public Integer getVenueId() {
		return venueId;
	}
	public void setVenueId(Integer venueId) {
		this.venueId = venueId;
	}
	public String getVenueName() {
		return venueName;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Integer getVenueAmount() {
		return venueAmount;
	}
	public void setVenueAmount(Integer venueAmount) {
		this.venueAmount = venueAmount;
	}
	
}
