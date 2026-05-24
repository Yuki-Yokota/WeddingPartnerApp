package com.example.weddingpartnerapp.model;
import java.util.List;
public class Guest {
	private Integer guestId;
	private Integer weddingId;
	private String guestName;
	private String mailAddress;
	private String precaution;
	private boolean attendance;
	private boolean ansFlg;
	private boolean deleteFlg;
	private List<Allergy>allergyInfo;
	
	public Guest() {}
	public Guest(Integer guestId, Integer weddingId, String guestName, String mailAddress, String precaution,
			boolean attendance, boolean ansFlg, boolean deleteFlg, List<Allergy> allergyInfo) {
		super();
		this.guestId = guestId;
		this.weddingId = weddingId;
		this.guestName = guestName;
		this.mailAddress = mailAddress;
		this.precaution = precaution;
		this.attendance = attendance;
		this.ansFlg = ansFlg;
		this.deleteFlg = deleteFlg;
		this.allergyInfo = allergyInfo;
	}
	public Integer getGuestId() {
		return guestId;
	}
	public void setGuestId(Integer guestId) {
		this.guestId = guestId;
	}
	public Integer getWeddingId() {
		return weddingId;
	}
	public void setWeddingId(Integer weddingId) {
		this.weddingId = weddingId;
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
	public boolean isAttendance() {
		return attendance;
	}
	public void setAttendance(boolean attendance) {
		this.attendance = attendance;
	}
	public boolean isAnsFlg() {
		return ansFlg;
	}
	public void setAnsFlg(boolean ansFlg) {
		this.ansFlg = ansFlg;
	}
	public boolean isDeleteFlg() {
		return deleteFlg;
	}
	public void setDeleteFlg(boolean deleteFlg) {
		this.deleteFlg = deleteFlg;
	}
	public List<Allergy> getAllergyInfo() {
		return allergyInfo;
	}
	public void setAllergyInfo(List<Allergy> allergyInfo) {
		this.allergyInfo = allergyInfo;
	}
	
}
