package com.example.weddingpartnerapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.Wedding;

@Mapper
public interface WeddingMapper {
	List<Wedding> findAllWedding();
	Wedding findByCustomerId(Integer customerId);
	Wedding findByWeddingId(Integer weddingId);
	Integer count();
	void insert(Wedding wed);
	void update(Wedding wed);
	void delete(@Param("weddingId")Integer weddingId);
}
