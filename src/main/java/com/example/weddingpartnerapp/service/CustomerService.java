package com.example.weddingpartnerapp.service;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.TodoDTO;

public interface CustomerService {
	List<CustomerDTO>findAllCustomer(SessionUser user);

	Paginated<CustomerDTO> sort(SessionUser user,String page, String sort);

	Paginated<CustomerDTO> search(SessionUser user,String page, String search);

	Combi<Paginated<CustomerDTO>> selectCustomer(SessionUser user,String page, String actionParam, String checkedId);

	CustomerDTO update(String checkedId, @Valid CustomerDTO customer);

	CustomerDTO register(SessionUser user,@Valid CustomerDTO customer);

	void delete(String checkedId);

	byte[] exportCsv(SessionUser user);

	Combi<List<CustomerDTO>> importCsv(MultipartFile file, SessionUser user);

	Paginated<CustomerDTO> registerMultiple(@Valid List<CustomerDTO> customerList);
	
	List<TodoDTO>findAllTodo(SessionUser user,String checkedId);

	Paginated<TodoDTO> transit(SessionUser user, String page, String checkedId);

	TodoDTO registerTodo(@Valid TodoDTO todo);
	
	TodoDTO updateTodo(String checkedId, @Valid TodoDTO todo);

	void complete(String checkedId);

}
