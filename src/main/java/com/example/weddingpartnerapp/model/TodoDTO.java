package com.example.weddingpartnerapp.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TodoDTO {
	private Integer todoId;
	private Integer customerId;
	
	private boolean status;
	
	@NotBlank(message = "{notblank}")
	private String taskContent;
	
	@NotNull(message = "{notnull}")
	@Future
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate deadline;
	
	private boolean deleteFlag;
	public TodoDTO(Todo todo) {
		super();
		this.todoId = todo.getTodoId();
		this.customerId = todo.getCustomerId();
		this.status = todo.isStatus();
		this.taskContent = todo.getTaskContent();
		this.deadline = todo.getDeadline();
		this.deleteFlag = todo.isDeleteFlag();
	}
	public TodoDTO() {}
	public Integer getTodoId() {
		return todoId;
	}
	public void setTodoId(Integer todoId) {
		this.todoId = todoId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getTaskContent() {
		return taskContent;
	}
	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}
	public LocalDate getDeadline() {
		return deadline;
	}
	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
	public boolean isDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}
