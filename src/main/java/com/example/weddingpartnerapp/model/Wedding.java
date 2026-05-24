package com.example.weddingpartnerapp.model;
import java.time.LocalDate;
public class Wedding {
	private Integer weddingId;
	private Integer customerId;
	private Integer venueId;
	private LocalDate weddingDate;
	private boolean completedFlag;
	
	public Wedding() {}
	
	public Wedding(Integer weddingId, Integer customerId, Integer venueId,LocalDate weddingDate,
			boolean completedFlag) {
		super();
		this.weddingId = weddingId;
		this.customerId = customerId;
		this.venueId = venueId;
		this.weddingDate = weddingDate;
		this.completedFlag = completedFlag;
	}
	
	public Integer getWeddingId() {
		return weddingId;
	}
	public void setWeddingId(Integer weddingId) {
		this.weddingId = weddingId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getVenueId() {
		return this.venueId;
	}
	public void setVenueId(Integer venueCode) {
		this.venueId = venueCode;
	}
	public boolean isCompletedFlag() {
		return completedFlag;
	}
	public void setCompletedFlag(boolean completedFlag) {
		this.completedFlag = completedFlag;
	}
	
	public LocalDate getWeddingDate() {
		return weddingDate;
	}
	public void setWeddingDate(LocalDate weddingDate) {
		this.weddingDate = weddingDate;
	}
	
}
