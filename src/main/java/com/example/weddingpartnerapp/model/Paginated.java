package com.example.weddingpartnerapp.model;
import java.util.List;
public class Paginated<T> {
	private Integer pageNum;
	private Integer totalPages;
	private List<T> list;
	
	public Paginated() {}
	
	public Paginated(Integer pageNum, Integer totalPages, List<T> list) {
		super();
		this.pageNum = pageNum;
		this.totalPages = totalPages;
		this.list = list;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	
}
