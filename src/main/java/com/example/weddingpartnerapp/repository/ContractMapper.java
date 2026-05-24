package com.example.weddingpartnerapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.weddingpartnerapp.model.BrideDress;
import com.example.weddingpartnerapp.model.Contract;
import com.example.weddingpartnerapp.model.Dish;
import com.example.weddingpartnerapp.model.Drink;
import com.example.weddingpartnerapp.model.GroomDress;
import com.example.weddingpartnerapp.model.OptionAmount;
import com.example.weddingpartnerapp.model.WeddingType;

@Mapper
public interface ContractMapper {
	List<Dish>findAllDish();
	List<Drink>findAllDrink();
	List<GroomDress>findAllGroomDress();
	List<BrideDress>findAllBrideDress();
	WeddingType findWeddingTypeByName(String weddingTypeName);
	OptionAmount findOptionAmountById(Integer optionAmountId);
	Contract findContractById(Integer contractId);
	Integer count();
	void insert(Contract contract);
	void update(Integer contractId);
}
