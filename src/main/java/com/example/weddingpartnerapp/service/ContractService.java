package com.example.weddingpartnerapp.service;

import java.util.List;

import com.example.weddingpartnerapp.model.Contract;

public interface ContractService {

	List<String> getVenueNameList();

	List<Integer> getDishAmountList();

	List<Integer> getDrinkAmountList();

	List<Integer> getGroomDressAmountList();

	List<Integer> getBrideDressAmountList();

	Contract registerContract(Contract data, String customerId);

	List<String> getWeddingTypeList(String id);

	void contract(String id);

	byte[] convertToPDF(String id,String funcName);

}
