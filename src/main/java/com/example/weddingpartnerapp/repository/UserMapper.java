package com.example.weddingpartnerapp.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.User;

@Mapper
public interface UserMapper {
	Optional<User> findUserByMailAddress(@Param("mailAddress") String mailAddress);
	List<User> findAllUser();
	void insert(User user);
	Integer count();
	Integer countByMailAddress(@Param("mailAddress")String mailAddress);
	User findById(@Param("userId")Integer userId);
	void update(User user);
	void delete(@Param("userId") Integer userId);
}
