package com.example.weddingpartnerapp.model;
public class OptionAmount {
	
	private Integer optionAmountId;
	private Integer photoShootAmount;
	private Integer videoShootAmount;
	private Integer flowerArrangementAmount;
	private Integer giftAmount;
	private Integer acousticAmount;
	private Integer hairMakeUpAmount;
	private Integer moderatorAmount;
	private Integer cakeAmount;
	
	public OptionAmount(Integer optionAmountId, Integer photoShootAmount, Integer videoShootAmount,
			Integer flowerArrangementAmount, Integer giftAmount, Integer acousticAmount, Integer hairMakeUpAmount,
			Integer moderatorAmount, Integer cakeAmount) {
		super();
		this.optionAmountId = optionAmountId;
		this.photoShootAmount = photoShootAmount;
		this.videoShootAmount = videoShootAmount;
		this.flowerArrangementAmount = flowerArrangementAmount;
		this.giftAmount = giftAmount;
		this.acousticAmount = acousticAmount;
		this.hairMakeUpAmount = hairMakeUpAmount;
		this.moderatorAmount = moderatorAmount;
		this.cakeAmount = cakeAmount;
	}
	public Integer getOptionAmountId() {
		return optionAmountId;
	}
	public void setOptionAmountId(Integer optionAmountId) {
		this.optionAmountId = optionAmountId;
	}
	public Integer getPhotoShootAmount() {
		return photoShootAmount;
	}
	public void setPhotoShootAmount(Integer photoShootAmount) {
		this.photoShootAmount = photoShootAmount;
	}
	public Integer getVideoShootAmount() {
		return videoShootAmount;
	}
	public void setVideoShootAmount(Integer videoShootAmount) {
		this.videoShootAmount = videoShootAmount;
	}
	public Integer getFlowerArrangementAmount() {
		return flowerArrangementAmount;
	}
	public void setFlowerArrangementAmount(Integer flowerArrangementAmount) {
		this.flowerArrangementAmount = flowerArrangementAmount;
	}
	public Integer getGiftAmount() {
		return giftAmount;
	}
	public void setGiftAmount(Integer giftAmount) {
		this.giftAmount = giftAmount;
	}
	public Integer getAcousticAmount() {
		return acousticAmount;
	}
	public void setAcousticAmount(Integer acousticAmount) {
		this.acousticAmount = acousticAmount;
	}
	public Integer getHairMakeUpAmount() {
		return hairMakeUpAmount;
	}
	public void setHairMakeUpAmount(Integer hairMakeUpAmount) {
		this.hairMakeUpAmount = hairMakeUpAmount;
	}
	public Integer getModeratorAmount() {
		return moderatorAmount;
	}
	public void setModeratorAmount(Integer moderatorAmount) {
		this.moderatorAmount = moderatorAmount;
	}
	public Integer getCakeAmount() {
		return cakeAmount;
	}
	public void setCakeAmount(Integer cakeAmount) {
		this.cakeAmount = cakeAmount;
	}
}
