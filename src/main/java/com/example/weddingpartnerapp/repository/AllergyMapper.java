package com.example.weddingpartnerapp.repository;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.weddingpartnerapp.model.Allergy;

@Mapper
public interface AllergyMapper {
	List<Allergy> findAllAllergy();
	List<Integer> findGuestAllergyById(List<Integer> allergyIds);
	void insertGuestAllergy(@Param("guestId")Integer guestId, @Param("allergyId")Integer allergyId);
	void deleteGuestAllergyByGuestId(@Param("guestId")Integer guestId);
}
