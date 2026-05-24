package com.example.weddingpartnerapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.Guest;

@Mapper
public interface GuestMapper {
	List<Guest> findByWeddingId(@Param("weddingId")Integer weddingId);
	Guest findByGuestId(@Param("guestId")Integer guestId);
	Integer count();
	void insert(Guest guest);
	void update(Guest guest);
	void delete(@Param("guestId") Integer guestId);
}
