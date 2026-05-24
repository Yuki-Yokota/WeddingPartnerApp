package com.example.weddingpartnerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.weddingpartnerapp.common.Action;
import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.common.WeddingFunction;
import com.example.weddingpartnerapp.model.CSVWedding;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Customer;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.Venue;
import com.example.weddingpartnerapp.model.Wedding;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.repository.CustomerMapper;
import com.example.weddingpartnerapp.repository.VenueMapper;
import com.example.weddingpartnerapp.repository.WeddingMapper;

import jakarta.validation.Valid;

@Service
public class WeddingServiceImpl implements WeddingService {
	
	@Autowired
	private WeddingMapper wedMapper;
	
	@Autowired
	private CustomerMapper cusMapper;
	
	@Autowired
	private VenueMapper venMapper;
	
	@Autowired
	private WeddingFunction wedFunc;

	@Override
	public List<WeddingDTO> findAll(SessionUser user) {
		List<WeddingDTO> weddingDTOList = new ArrayList<>();
		List<Wedding> weddingList = wedMapper.findAllWedding();
		List<Customer>customerList = new ArrayList<>();
		
		if(weddingList.isEmpty()) {
			throw new ApplicationException(ErrorCode.LIST_IS_EMPTY);
		}
		if(user.getRole().equals("user")) { 
			//ユーザID(プランナー)に紐づく顧客リスト(複数も想定)からIDのみ取得
			customerList=cusMapper.findByUserId(user.getUserId());
			List<Integer> customerIds = customerList.stream()
													.map(Customer::getCustomerId)
													.collect(Collectors.toList());
			//式全件からユーザが請け負う式(顧客紐づくもの)のみ取得
			weddingList = weddingList.stream()
									.filter(wed->customerIds.contains(wed.getCustomerId()))
									.collect(Collectors.toList());
		}
		for(Wedding wed : weddingList) {
			weddingDTOList.add(wedFunc.apply(wed));
		}
		return weddingDTOList;
	}

	@Override
	public Paginated<WeddingDTO> sort(String page, String sort, SessionUser user) {
		List<WeddingDTO>weddingList = findAll(user);
		String[] getSortAction=new String[2];
		Comparator<WeddingDTO> comparator = null;
		if(sort!=null){
			getSortAction = sort.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String sortAction=getSortAction[0];
		String attribute=getSortAction[1];
		switch(attribute) {
		case "weddingId":
			comparator = Comparator.comparing(wed->wed.getWeddingId(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "groomName":
			comparator = Comparator.comparing(wed->wed.getGroomName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "brideName":
			comparator = Comparator.comparing(wed->wed.getBrideName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "plannerName":
			comparator = Comparator.comparing(wed->wed.getPlannerName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "weddingDate":
			comparator = Comparator.comparing(wed->wed.getWeddingDate(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "venueName":
			comparator = Comparator.comparing(wed->wed.getVenueName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		if("DESC".equals(sortAction)) {
			comparator=comparator.reversed();
		}
		weddingList=Util.sort(weddingList, comparator);
		return Util.pagenation(weddingList, page);
	}

	@Override
	public Paginated<WeddingDTO> search(String page, String search, SessionUser user) {
		List<WeddingDTO>weddingList = findAll(user);
		String[] getFilterAction=new String[2];
		if(search!=null){
			getFilterAction = search.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String entryValue=getFilterAction[0].replaceAll("\\u3000| ","").toUpperCase();
		String attribute=getFilterAction[1];
		Function<WeddingDTO,String> func = null;
		switch(attribute) {
		case "groomName":
			func=wed->wed.getGroomName();
			break;
		case "brideName":
			func=wed->wed.getBrideName();
			break;
		case "plannerName":
			func=wed->wed.getPlannerName();
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		weddingList=Util.search(weddingList, entryValue,func);
		if(weddingList.isEmpty()) {
			weddingList = findAll(user);
		}
		return Util.pagenation(weddingList, page);
	}

	@Override
	public Combi<Paginated<WeddingDTO>> selectWedding(String page, String actionParam, String checkedId,
			SessionUser user) {
		List<WeddingDTO> weddingList = findAll(user);
		Action action=null;
    	String nextForm=null;
    	
		action = Action.form(actionParam);
		switch(action) {
		case UPDATE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "weddingUpdate";
			break;
		case DELETE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "weddingDelete";
			break;
		default:
			break;
		}
		
		Paginated<WeddingDTO> pagenated = Util.pagenation(weddingList, page);
		return new Combi<Paginated<WeddingDTO>>(nextForm,pagenated);
	}

	@Override
	public WeddingDTO update(String checkedId, @Valid WeddingDTO wedDto) {
		Wedding wed = new Wedding();
		Integer id = Integer.parseInt(checkedId);
		wed.setWeddingId(id);
		wed.setWeddingDate(wedDto.getWeddingDate());
		String venueName = wedDto.getVenueName();
		wed.setVenueId(venMapper.findByName(venueName).getVenueId());
		wedMapper.update(wed);
		wed = wedMapper.findByWeddingId(id);
		wedDto = wedFunc.apply(wed);
		return wedDto;
	}

	@Override
	public void delete(String checkedId) {
		Integer weddingId = Integer.parseInt(checkedId);
		wedMapper.delete(weddingId);
		
	}

	/**
	 * 会場名リストを取得
	 */
	@Override
	public List<String> getVenueNameList() {
		return venMapper.findAllVenue().stream()
				.map(Venue::getVenueName)
				.collect(Collectors.toList());
	}

	@Override
	public byte[] exportCsv(SessionUser user) {
		List<WeddingDTO> weddingList = findAll(user);
		if(weddingList.isEmpty()) {
			throw new ApplicationException(ErrorCode.LIST_IS_EMPTY);
		}
	    List<CSVWedding> csvList = weddingList.stream().map(
	        e -> new CSVWedding(e)
	    ).collect(Collectors.toList());
		return CSVDownload.getCsvFile(csvList,CSVWedding.class);
	}

}
