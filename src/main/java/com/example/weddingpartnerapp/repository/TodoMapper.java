package com.example.weddingpartnerapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.Todo;

@Mapper
public interface TodoMapper {
	List<Todo> findAllTodo();
	List<Todo> findByCustomerId(@Param("customerId")Integer customerId);
	Todo findByTodoId(@Param("todoId")Integer todoId);
	Integer count();
	void existCustomerById(@Param("customerId")Integer customerId);
	void insert(Todo todo);
	void update(Todo todo);
	void delete(@Param("todoId")Integer todoId);
}
