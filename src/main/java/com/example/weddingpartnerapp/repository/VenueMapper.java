package com.example.weddingpartnerapp.repository;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.weddingpartnerapp.model.Venue;
import com.example.weddingpartnerapp.model.WeddingType;

@Mapper
public interface VenueMapper {
	List<Venue> findAllVenue();
	Venue findById(Integer venueId);
	Venue findByName(String venueName);
	List<WeddingType> findAllWeddingType(String venueName);
}
