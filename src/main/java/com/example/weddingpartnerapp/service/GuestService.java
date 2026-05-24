package com.example.weddingpartnerapp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.GuestDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;

import jakarta.validation.Valid;

public interface GuestService {

	List<GuestDTO> findAll(String selectWeddingId);

	Paginated<GuestDTO> sort(String page, String sort, String selectWeddingId);

	Paginated<GuestDTO> search(String page, String search, String selectWeddingId);

	Combi<Paginated<GuestDTO>> selectGuest(String page, String actionParam, String checkedId, String selectWeddingId);

	@Valid
	GuestDTO register(@Valid GuestDTO guest, String selectWeddingId);
	
	Paginated<GuestDTO> registerMultiple(@Valid List<GuestDTO> guestList, String selectWeddingId);
	
	void registerAllergy(GuestDTO guest);

	Object update(String checkedId, @Valid String selectWeddingId,GuestDTO guest);

	void delete(String checkedId);

	byte[] exportCsv(String sselectWeddingId);

	Combi<List<GuestDTO>> importCsv(MultipartFile file, String selectWeddingId);

	void mailDistribution(String checkedId, SessionUser user,String selectWeddingId);

}
