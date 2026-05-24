package com.example.weddingpartnerapp.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Contract {
	private Integer contractId;
	private Integer customerId;
	
	@NotNull(message = "{notnull}")
	@Digits(integer = 3, fraction = 0,message="{digits}")
	private Integer numOfPerson;//パラメタ
	
	@Pattern(regexp="[^-]+",message="{choice}")
	private String venueName;
	private String weddingType;//計算対象
	
	@NotNull(message = "{notnull}")
	@Future
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate weddingDate;
	private Integer dish;//計算対象
	private Integer drink;//計算対象
	private Integer groomDress;//計算対象
	private Integer brideDress;//計算対象
	private boolean photoShootFlg;//計算対象
	private boolean videoShootFlg;//計算対象
	private boolean flowerArrangementFlg;//計算対象
	private boolean giftFlg;//計算対象
	private boolean acousticFlg;//計算対象
	private boolean hairMakeUpFlg;//計算対象
	private boolean moderatorFlg;//計算対象
	private String otherText;
	private Integer totalAmount;
	private boolean completedFlg;
	
	public Contract(Integer contractId, Integer customerId, Integer numOfPerson, String venueName,
			String weddingType, LocalDate weddingDate, Integer dish, Integer drink, Integer groomDress, Integer brideDress,
			boolean photoShootFlg, boolean videoShootFlg, boolean flowerArrangementFlg, boolean giftFlg,
			boolean acousticFlg, boolean hairMakeUpFlg, boolean moderatorFlg, String otherText, Integer totalAmount,
			boolean completedFlg) {
		super();
		this.contractId = contractId;
		this.customerId = customerId;
		this.numOfPerson = numOfPerson;
		this.venueName = venueName;
		this.weddingType = weddingType;
		this.weddingDate = weddingDate;
		this.dish = dish;
		this.drink = drink;
		this.groomDress = groomDress;
		this.brideDress = brideDress;
		this.photoShootFlg = photoShootFlg;
		this.videoShootFlg = videoShootFlg;
		this.flowerArrangementFlg = flowerArrangementFlg;
		this.giftFlg = giftFlg;
		this.acousticFlg = acousticFlg;
		this.hairMakeUpFlg = hairMakeUpFlg;
		this.moderatorFlg = moderatorFlg;
		this.otherText = otherText;
		this.totalAmount = totalAmount;
		this.completedFlg = completedFlg;
	}
	public Contract() {}
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getNumOfPerson() {
		return numOfPerson;
	}
	public void setNumOfPerson(Integer numOfPerson) {
		this.numOfPerson = numOfPerson;
	}
	public String getVenueName() {
		return venueName;
	}
	public String getWeddingType() {
		return weddingType;
	}
	public void setWeddingType(String weddingType) {
		this.weddingType = weddingType;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	public LocalDate getWeddingDate() {
		return weddingDate;
	}
	public void setWeddingDate(LocalDate weddingDate) {
		this.weddingDate = weddingDate;
	}
	public Integer getDish() {
		return dish;
	}
	public void setDish(Integer dish) {
		this.dish = dish;
	}
	public Integer getDrink() {
		return drink;
	}
	public void setDrink(Integer drink) {
		this.drink = drink;
	}
	public Integer getGroomDress() {
		return groomDress;
	}
	public void setGroomDress(Integer groomDress) {
		this.groomDress = groomDress;
	}
	public Integer getBrideDress() {
		return brideDress;
	}
	public void setBrideDress(Integer brideDress) {
		this.brideDress = brideDress;
	}
	public boolean isPhotoShootFlg() {
		return photoShootFlg;
	}
	public void setPhotoShootFlg(boolean photoShootFlg) {
		this.photoShootFlg = photoShootFlg;
	}
	public boolean isVideoShootFlg() {
		return videoShootFlg;
	}
	public void setVideoShootFlg(boolean videoShootFlg) {
		this.videoShootFlg = videoShootFlg;
	}
	public boolean isFlowerArrangementFlg() {
		return flowerArrangementFlg;
	}
	public void setFlowerArrangementFlg(boolean flowerArrangementFlg) {
		this.flowerArrangementFlg = flowerArrangementFlg;
	}
	public boolean isGiftFlg() {
		return giftFlg;
	}
	public void setGiftFlg(boolean giftFlg) {
		this.giftFlg = giftFlg;
	}
	public boolean isAcousticFlg() {
		return acousticFlg;
	}
	public void setAcousticFlg(boolean acousticFlg) {
		this.acousticFlg = acousticFlg;
	}
	public boolean isHairMakeUpFlg() {
		return hairMakeUpFlg;
	}
	public void setHairMakeUpFlg(boolean hairMakeUpFlg) {
		this.hairMakeUpFlg = hairMakeUpFlg;
	}
	public boolean isModeratorFlg() {
		return moderatorFlg;
	}
	public void setModeratorFlg(boolean moderatorFlg) {
		this.moderatorFlg = moderatorFlg;
	}
	public String getOtherText() {
		return otherText;
	}
	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public boolean isCompletedFlg() {
		return completedFlg;
	}
	public void setCompletedFlg(boolean completedFlg) {
		this.completedFlg = completedFlg;
	}
	
}
