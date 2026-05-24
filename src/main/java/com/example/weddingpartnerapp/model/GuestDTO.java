package com.example.weddingpartnerapp.model;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class GuestDTO {
	private Integer guestId;
	
	@NotBlank(message = "{notblank}")
	private String guestName;
	
	@Email(message = "{mailaddress}")
	private String mailAddress;
	private String precaution;
	private String attendance;
	private String ansFlg;
	private List<Allergy>allergyInfo;
	public GuestDTO(Integer guestId, Integer weddingId, String guestName, String mailAddress,String precaution, String attendance,
			String ansFlg, boolean deleteFlg, List<Allergy> allergyInfo) {
		super();
		this.guestId = guestId;
		this.guestName = guestName;
		this.mailAddress = mailAddress;
		this.precaution = precaution;
		this.attendance = attendance;
		this.ansFlg = ansFlg;
		this.allergyInfo = allergyInfo;
	}
	
	public GuestDTO(CSVGuest guest) {
		super();
		this.guestId = guest.getGuestId();
		this.guestName = guest.getGuestName();
		this.mailAddress = guest.getMailAddress();
		this.precaution = guest.getPrecaution();
		this.attendance = guest.getAttendance();
		this.ansFlg = guest.getAnsFlg();
	}
	public GuestDTO() {}
	public Integer getGuestId() {
		return guestId;
	}
	public void setGuestId(Integer guestId) {
		this.guestId = guestId;
	}
	
	public String getGuestName() {
		return guestName;
	}
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getPrecaution() {
		return precaution;
	}
	public void setPrecaution(String precaution) {
		this.precaution = precaution;
	}
	public String getAttendance() {
		return attendance;
	}
	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	public String getAnsFlg() {
		return ansFlg;
	}
	public void setAnsFlg(String ansFlg) {
		this.ansFlg = ansFlg;
	}
	
	public List<Allergy> getAllergyInfo() {
		return allergyInfo;
	}
	public void setAllergyInfo(List<Allergy> allergyInfo) {
		this.allergyInfo = allergyInfo;
	}
	public void addAllergyName(String allergyName) {
	    Allergy allergy = new Allergy();
	    allergy.setAllergyName(allergyName);
	    this.allergyInfo.add(allergy);
	}
}
