package com.example.weddingpartnerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.Action;
import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.CSVUpload;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.CSVCustomer;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Customer;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.Todo;
import com.example.weddingpartnerapp.model.TodoDTO;
import com.example.weddingpartnerapp.repository.CustomerMapper;
import com.example.weddingpartnerapp.repository.TodoMapper;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerMapper cusMapper;
	
	@Autowired
	private TodoMapper todoMapper;
	
	@Override
	public List<CustomerDTO> findAllCustomer(SessionUser user) {
		List<Customer>customerList = new ArrayList<>();
		if(user.getRole().equals("admin"))
			customerList = cusMapper.findAllCustomer();
		else {
			customerList = cusMapper.findByUserId(user.getUserId());
		}
		List<CustomerDTO> customerDTOList = customerList.stream()
			.map(CustomerDTO::new)
			.collect(Collectors.toList());
		
		return customerDTOList;
	}

	@Override
	public Paginated<CustomerDTO> sort(SessionUser user,String page, String sort) {
		List<CustomerDTO> customerList = findAllCustomer(user);
		String[] getSortAction=new String[2];
		Comparator<CustomerDTO> comparator = null;
		if(sort!=null){
			getSortAction = sort.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String sortAction=getSortAction[0];
		String attribute=getSortAction[1];
		switch(attribute) {
		case "customerId":
			comparator = Comparator.comparing(customer->customer.getCustomerId(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "groomName":
			comparator = Comparator.comparing(customer->customer.getGroomName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "brideName":
			comparator = Comparator.comparing(customer->customer.getBrideName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "mailAddress":
			comparator = Comparator.comparing(customer->customer.getMailAddress(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "phoneNumber":
			comparator = Comparator.comparing(customer->customer.getPhoneNumber(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		if("DESC".equals(sortAction)) {
			comparator=comparator.reversed();
		}
		customerList=Util.sort(customerList, comparator);
		return Util.pagenation(customerList, page);
	}

	@Override
	public Paginated<CustomerDTO> search(SessionUser user,String page, String search) {
		List<CustomerDTO> customerList = findAllCustomer(user);
		String[] getFilterAction=new String[2];
		if(search!=null){
			getFilterAction = search.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String entryValue=getFilterAction[0].replaceAll("\\u3000| ","").toUpperCase();
		String attribute=getFilterAction[1];
		Function<CustomerDTO,String> func = null;
		switch(attribute) {
		case "groomName":
			func=customer->customer.getGroomName();
			break;
		case "brideName":
			func=customer->customer.getBrideName();
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		customerList=Util.search(customerList, entryValue,func);
		if(customerList.isEmpty()) {
			customerList = findAllCustomer(user);
		}
		return Util.pagenation(customerList, page);
	}

	@Override
	public Combi<Paginated<CustomerDTO>> selectCustomer(SessionUser user,String page, String actionParam, String checkedId) {
		List<CustomerDTO> customerList = findAllCustomer(user);
		Action action=null;
    	String nextForm=null;
    	
		action = Action.form(actionParam);
		switch(action) {
		case REGISTER_FORM:
			nextForm = "customerRegister";
			break;
		case UPDATE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "customerUpdate";
			break;
		case DELETE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "customerDelete";
			break;
		case CSV_IMPORT_FORM:
			nextForm = "csvImport";
			break;
		default:
			break;
		}
		
		Paginated<CustomerDTO> pagenated = Util.pagenation(customerList, page);
		return new Combi<Paginated<CustomerDTO>>(nextForm,pagenated);
	}
	
	@Override
	public CustomerDTO register(SessionUser user,CustomerDTO cusDto) {
		Integer customerId = cusMapper.count();
		if(customerId!=null) {
			customerId = customerId + 1;
		}
		else {
			customerId = 1;
		}
		cusDto.setCustomerId(customerId);
		cusDto.setUserId(user.getUserId());
		Customer customer = new Customer(cusDto);
		cusMapper.insert(customer);
		customer = cusMapper.findByCustomerId(customerId);
		cusDto = new CustomerDTO(customer);
		return cusDto;
	}
	
	@Override
	public Paginated<CustomerDTO> registerMultiple(@Valid List<CustomerDTO> customerList) {
		for(CustomerDTO cusDto : customerList) {
			Customer customer = new Customer(cusDto);
			cusMapper.insert(customer);
		}
		return Util.pagenation(customerList, null);
	}

	@Override
	public CustomerDTO update(String checkedId, @Valid CustomerDTO cusDto) {
		Integer customerId = Integer.parseInt(checkedId);
		cusDto.setCustomerId(customerId);
		Customer customer = new Customer(cusDto);
		cusMapper.update(customer);
		customer = cusMapper.findByCustomerId(customerId);
		cusDto = new CustomerDTO(customer);
		return cusDto;
	}

	@Override
	public void delete(String checkedId) {
		Integer customerId = Integer.parseInt(checkedId);
		cusMapper.delete(customerId);
	}

	@Override
	public byte[] exportCsv(SessionUser user) {
		List<CustomerDTO> customerList = findAllCustomer(user);
	    List<CSVCustomer> csvList = customerList.stream()
	    		.map(CSVCustomer::new)
	    		.collect(Collectors.toList());
		return CSVDownload.getCsvFile(csvList,CSVCustomer.class);
	}

	@Override
	public Combi<List<CustomerDTO>> importCsv(MultipartFile file, SessionUser user) {
		List<CSVCustomer> csvBeforeList = CSVUpload.csvUpload(file,CSVCustomer.class);
		List<CustomerDTO> csvList = new ArrayList<>();
		
		for(CSVCustomer e : csvBeforeList) {
			csvList.add(new CustomerDTO(e));
		}
		
		List<CustomerDTO> customerList = findAllCustomer(user);
		
		int updateCnt = 0;
		int csvMaxId = csvList.stream()
		        .map(CustomerDTO::getCustomerId)
		        .max(Integer::compare)
		        .orElse(0);
		int newCustomerId=Math.max(cusMapper.count(),csvMaxId)+1;
		Set<Integer> usedcustomerIds = customerList.stream()
										.map(CustomerDTO::getCustomerId)
										.collect(Collectors.toSet());
		for (CustomerDTO customer : csvList) {
		    Integer customerId = customer.getCustomerId();
		    if (usedcustomerIds.contains(customerId)) {
		    	customer.setCustomerId(newCustomerId);
			    usedcustomerIds.add(newCustomerId);
		        updateCnt++;
		        newCustomerId++;
		    }
		    else {
			    usedcustomerIds.add(customerId);
		    }
		}
		Combi<List<CustomerDTO>>result = new Combi<>();
		result.setKey(String.valueOf(updateCnt));
		result.setData(csvList);
		return result;
	}

	@Override
	public Paginated<TodoDTO> transit(SessionUser user, String page,String checkedId) {
		List<TodoDTO>todoList = findAllTodo(user,checkedId);
		return  Util.pagenation(todoList, page);
	}

	@Override
	public List<TodoDTO> findAllTodo(SessionUser user,String checkedId) {
		List<Todo> todoList = new ArrayList<>();
		List<TodoDTO>todoDtoList = new ArrayList<>();
		if(user.getRole().equals("admin")) {
			todoList = todoMapper.findAllTodo();
		}
		else {
			Integer customerId = Integer.parseInt(checkedId);
			todoList = todoMapper.findByCustomerId(customerId);
		}
		for(Todo todo : todoList) {
			TodoDTO todoDto = new TodoDTO(todo);
			todoDtoList.add(todoDto);
		}
		return todoDtoList;
	}

	@Override
	public TodoDTO registerTodo(@Valid TodoDTO todoDto) {
		todoMapper.existCustomerById(todoDto.getCustomerId());
		Integer todoId = todoMapper.count();
		if(todoId!=null) {
			todoId = todoId + 1;
		}
		else {
			todoId = 1;
		}
		todoDto.setTodoId(todoId);
		Todo todo = new Todo(todoDto);
		todoMapper.insert(todo);
		todo = todoMapper.findByTodoId(todoId);
		todoDto = new TodoDTO(todo);
		return todoDto;
	}
	
	@Override
	public TodoDTO updateTodo(String checkedId, @Valid TodoDTO todoDto) {
		Todo todo = new Todo(todoDto);
		todoMapper.update(todo);
		todo = todoMapper.findByTodoId(todo.getTodoId());
		todoDto = new TodoDTO(todo);
		return todoDto;
	}

	@Override
	public void complete(String checkedId) {
		Integer todoId = Integer.parseInt(checkedId);
		todoMapper.delete(todoId);
	}
}
