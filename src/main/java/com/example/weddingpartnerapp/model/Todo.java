package com.example.weddingpartnerapp.model;
import java.time.LocalDate;
public class Todo {
	private Integer todoId;
	private Integer customerId;
	private boolean status;
	private String taskContent;
	private transient LocalDate deadline;
	private boolean deleteFlag;
	
	public Todo() {}

	public Todo(TodoDTO todoDto) {
		super();
		this.todoId = todoDto.getTodoId();
		this.customerId = todoDto.getCustomerId();
		this.status = todoDto.isStatus();
		this.taskContent = todoDto.getTaskContent();
		this.deadline = todoDto.getDeadline();
		this.deleteFlag = todoDto.isDeleteFlag();
	}

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
