package com.example.weddingpartnerapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.Customer;

@Mapper
public interface CustomerMapper {
	List<Customer> findAllCustomer();
	List<Customer> findByUserId(@Param("userId")Integer userId);
	Customer findByCustomerId(@Param("customerId")Integer customerId);
	Integer count();
	void insert(Customer customer);
	void update(Customer customer);
	void delete(@Param("customerId") Integer customerId);
}
