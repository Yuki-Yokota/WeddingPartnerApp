package com.example.weddingpartnerapp.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class WeddingDTO {
	private Integer weddingId;
	private String groomName;
	private String brideName;
	private String plannerName;
	
	@NotNull(message = "{notnull}")
	@Future
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate weddingDate;
	
	private String venueName;
	
	public WeddingDTO() {}
	
	public WeddingDTO(Integer weddingId, String groomName, String brideName, String plannerName, LocalDate weddingDate,
			String venueName) {
		super();
		this.weddingId = weddingId;
		this.groomName = groomName;
		this.brideName = brideName;
		this.plannerName = plannerName;
		this.weddingDate = weddingDate;
		this.venueName = venueName;
	}
	public Integer getWeddingId() {
		return weddingId;
	}
	public void setWeddingId(Integer weddingId) {
		this.weddingId = weddingId;
	}
	public String getGroomName() {
		return groomName;
	}
	public void setGroomName(String groomName) {
		this.groomName = groomName;
	}
	public String getBrideName() {
		return brideName;
	}
	public void setBrideName(String brideName) {
		this.brideName = brideName;
	}
	public String getPlannerName() {
		return plannerName;
	}
	public void setPlannerName(String plannerName) {
		this.plannerName = plannerName;
	}
	public LocalDate getWeddingDate() {
		return weddingDate;
	}
	public void setWeddingDate(LocalDate weddingDate) {
		this.weddingDate = weddingDate;
	}
	public String getVenueName() {
		return venueName;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	
	
}
