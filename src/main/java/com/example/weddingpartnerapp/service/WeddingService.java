package com.example.weddingpartnerapp.service;

import java.util.List;

import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.WeddingDTO;

import jakarta.validation.Valid;

public interface WeddingService {

	List<WeddingDTO> findAll(SessionUser user);

	Paginated<WeddingDTO> sort(String page, String sort,SessionUser user);

	Paginated<WeddingDTO> search(String page, String search,SessionUser user);

	Combi<Paginated<WeddingDTO>> selectWedding(String page, String actionParam, String checkedId,SessionUser user);

	Object update(String checkedId, @Valid WeddingDTO wedding);

	void delete(String checkedId);

	List<String> getVenueNameList();

	byte[] exportCsv(SessionUser user);

}
