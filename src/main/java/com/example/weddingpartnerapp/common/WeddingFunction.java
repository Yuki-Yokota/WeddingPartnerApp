package com.example.weddingpartnerapp.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.weddingpartnerapp.model.Customer;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.model.Venue;
import com.example.weddingpartnerapp.model.Wedding;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.repository.CustomerMapper;
import com.example.weddingpartnerapp.repository.UserMapper;
import com.example.weddingpartnerapp.repository.VenueMapper;
import com.example.weddingpartnerapp.repository.WeddingMapper;

@Component
public class WeddingFunction implements ThrowExcepFunction<Wedding,WeddingDTO>{
	
	@Autowired
	public WeddingMapper wedMapper;
	
	@Autowired
	public VenueMapper venMapper;
	
	@Autowired
	public CustomerMapper cusMapper;
	
	@Autowired
	public UserMapper userMapper;

	@Override
	public WeddingDTO apply(Wedding wed) throws ApplicationException {
		WeddingDTO wedDto = new WeddingDTO();
		wedDto.setWeddingId(wed.getWeddingId());
		Integer customerId = wed.getCustomerId();
		Integer venueId = wed.getVenueId();
		Venue venue = venMapper.findById(venueId);
		wedDto.setVenueName(venue.getVenueName());
		Customer customer = cusMapper.findByCustomerId(customerId);
		wedDto.setGroomName(customer.getGroomName());
		wedDto.setBrideName(customer.getBrideName());
		User userTemp = userMapper.findById(customer.getUserId());
		wedDto.setPlannerName(userTemp.getUserName());
		wedDto.setWeddingDate(wed.getWeddingDate());
		return wedDto;
	}
}
