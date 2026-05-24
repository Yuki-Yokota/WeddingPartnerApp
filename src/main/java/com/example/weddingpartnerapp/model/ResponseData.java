package com.example.weddingpartnerapp.model;
public class ResponseData<T>{
	private T model;
   private Paginated<T> pageInfo;
	private String nextForm;
	private Integer csvUploadLim;
	public ResponseData(T model, Paginated<T> pageInfo, String nextForm, Integer csvUploadLim) {
		super();
		this.model = model;
		this.pageInfo = pageInfo;
		this.nextForm = nextForm;
		this.csvUploadLim = csvUploadLim;
	}
	public T getModel() {
		return model;
	}
	public void setModel(T model) {
		this.model = model;
	}
	public Paginated<T> getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(Paginated<T> pageInfo) {
		this.pageInfo = pageInfo;
	}
	
	public String getNextForm() {
		return nextForm;
	}
	
	public void setNextForm(String nextForm) {
		this.nextForm = nextForm;
	}
	public Integer getCsvUploadLim() {
		return csvUploadLim;
	}
	public void setCsvUploadLim(Integer csvUploadLim) {
		this.csvUploadLim = csvUploadLim;
	}
	
}
