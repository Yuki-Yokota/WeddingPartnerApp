package com.example.weddingpartnerapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.TodoDTO;
import com.example.weddingpartnerapp.service.CustomerService;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/customer")
    public String init(Model model,HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
    	List<CustomerDTO>customerList = customerService.findAllCustomer(user);
    	Paginated<CustomerDTO>paginated = Util.pagenation(customerList, null);
    	model.addAttribute("customerList", paginated.getList());
    	model.addAttribute("pageNum", paginated.getPageNum());
    	model.addAttribute("totalPages", paginated.getTotalPages());
		return "customer";
    }
	
	@GetMapping(value="/api/customer",params="sort")
	@ResponseBody
    public ResponseEntity<Object> sort(HttpSession session,@RequestParam String page,@RequestParam String sort) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Paginated<CustomerDTO> customerList = customerService.sort(user, page,sort);
		ResponseData<CustomerDTO> response = new ResponseData<>(null,customerList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/customer",params="search")
	@ResponseBody
    public ResponseEntity<Object> search(HttpSession session,@RequestParam String page,@RequestParam String search) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Paginated<CustomerDTO> customerList = customerService.search(user,page, search);
		ResponseData<CustomerDTO> response = new ResponseData<>(null,customerList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/customer",params="form")
	@ResponseBody
    public ResponseEntity<Object> transit(HttpSession session,
    		@RequestParam String page,
    		@RequestParam String form,
    		@RequestParam(name="check") String checkedId) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Paginated<TodoDTO> todoList = customerService.transit(user, page,checkedId);
		ResponseData<TodoDTO> response = new ResponseData<>(null,todoList,"todoForm",null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/customer",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> selectCustomer(
    		HttpSession session,
    		@RequestParam String page,
    		@RequestParam(name="operation",required=false) String actionParam,
    		@RequestParam(name="check",required=false) String checkedId){
		SessionUser user = (SessionUser)session.getAttribute("user");
    	Combi<Paginated<CustomerDTO>> combi = customerService.selectCustomer(user,page, actionParam, checkedId);
    	String nextForm = combi.getKey();
		Paginated<CustomerDTO>customerList = combi.getData();
		ResponseData<CustomerDTO> response = new ResponseData<>(null,customerList,nextForm,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@PostMapping(value="/api/customer",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> register(HttpSession session,Model model,
    		@Valid @RequestBody CustomerDTO customer) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		customer = customerService.register(user,customer);
    	ResponseData<CustomerDTO> response = new ResponseData<>(customer,null,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@SuppressWarnings("unchecked")
	@PostMapping(value="/api/customer/batch",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> csvRegister(Model model,HttpSession session) {
		Paginated<CustomerDTO> pages = (Paginated<CustomerDTO>)session.getAttribute("csvList");
		pages = customerService.registerMultiple(pages.getList());
		session.removeAttribute("csvList");
    	ResponseData<CustomerDTO> response = new ResponseData<>(null,pages,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value="/api/customer/todo",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> registerTodo(HttpSession session,Model model,
    		@Valid @RequestBody TodoDTO todo) {
		
		todo = customerService.registerTodo(todo);
    	ResponseData<TodoDTO> response = new ResponseData<>(todo,null,"todoForm",null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@PutMapping(value="/api/customer/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> update(Model model,
    		@Valid @RequestBody CustomerDTO customer,
    		@PathVariable("id") String checkedId) {
		ResponseData<CustomerDTO> response = new ResponseData<>(customerService.update(checkedId,customer),null,null,null);
    	return ResponseEntity.status(HttpStatus.OK).body(response);
    	
    }
	
	@PutMapping(value="/api/customer/todo/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> updateTodo(Model model,
    		@Valid @RequestBody TodoDTO todo,
    		@PathVariable("id") String checkedId) {
		ResponseData<TodoDTO> response = new ResponseData<>(customerService.updateTodo(checkedId,todo),null,"todoForm",null);
    	return ResponseEntity.status(HttpStatus.OK).body(response);
    	
    }
	
	@DeleteMapping(value="/api/customer/{id}")
    @ResponseBody
    public ResponseEntity<Object> delete(Model model, @PathVariable("id") String checkedId) {	
    	customerService.delete(checkedId);
    	return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value="/api/customer/todo/{id}")
    @ResponseBody
    public ResponseEntity<Object> deleteTodo(Model model, @PathVariable("id") String checkedId) {	
    	customerService.complete(checkedId);
    	return ResponseEntity.noContent().build();
	}
}
