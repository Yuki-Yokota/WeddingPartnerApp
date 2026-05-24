package com.example.weddingpartnerapp.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "新郎名","新婦名","プランナー名", "式日付", "会場名"})
public class CSVWedding {
	
	@JsonProperty("ID")
	private Integer weddingId;
	
	@JsonProperty("新郎名")
	private String groomName;
	
	@JsonProperty("新婦名")
	private String brideName;
	
	@JsonProperty("プランナー名")
	private String plannerName;
	
	@JsonProperty("式日付")
	private LocalDate weddingDate;
	
	@JsonProperty("会場名")
	private String venueName;

	public CSVWedding(WeddingDTO wedDto) {
		super();
		this.weddingId = wedDto.getWeddingId();
		this.groomName = wedDto.getGroomName();
		this.brideName = wedDto.getBrideName();
		this.plannerName = wedDto.getPlannerName();
		this.weddingDate = wedDto.getWeddingDate();
		this.venueName = wedDto.getVenueName();
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
