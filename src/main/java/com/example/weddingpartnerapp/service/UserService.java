package com.example.weddingpartnerapp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.User;

public interface UserService {
	List<User> findAll();

	User update(String checkedId, User data) ;

	User register(User data);
	
	Paginated<User> registerMultiple(List<User> list);

	void delete(String checkedId);

	Paginated<User> sort(String page, String sortKey);

	Paginated<User> search(String page,String searchValue);

	Paginated<User> filter(String page, String filterValue);
	
	Combi<Paginated<User>> selectUser(String page, String operation,String checkedId);

	byte[] exportCsv();

	Combi<List<User>> importCsv(MultipartFile file);

	User authenticate(User user) throws ApplicationException;

}
