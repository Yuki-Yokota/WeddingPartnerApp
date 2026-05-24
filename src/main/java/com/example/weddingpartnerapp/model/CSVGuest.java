package com.example.weddingpartnerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "ゲスト名","メールアドレス","出欠", "アンケート回答有無", "注意事項"})
public class CSVGuest {
	@JsonProperty("ID")
	@NotNull(message = "{notnull}")
	private Integer guestId;
	
	@JsonProperty("ゲスト名")
	@NotBlank(message = "{notblank}")
	private String guestName;
	
	@JsonProperty("メールアドレス")
	@Email(message = "{mailaddress}")
	private String mailAddress;
	
	@JsonProperty("出欠")
	@Pattern(regexp="^[出欠]$")
	@NotBlank(message = "{notblank}")
	private String attendance;
	
	@JsonProperty("アンケート回答有無")
	@Pattern(regexp="^[有無]$")
	@NotBlank(message = "{notblank}")
	private String ansFlg;
	
	@JsonProperty("注意事項")
	private String precaution;

	public CSVGuest(GuestDTO gstDto) {
		super();
		this.guestId = gstDto.getGuestId();
		this.guestName = gstDto.getGuestName();
		this.mailAddress = gstDto.getMailAddress();
		this.attendance = gstDto.getAttendance();
		this.ansFlg = gstDto.getAnsFlg();
		this.precaution = gstDto.getPrecaution();
	}

	public CSVGuest() {}

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

	public String getPrecaution() {
		return precaution;
	}

	public void setPrecaution(String precaution) {
		this.precaution = precaution;
	}
	
	
}
