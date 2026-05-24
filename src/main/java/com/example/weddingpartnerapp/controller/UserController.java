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
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/user")
    public String init(Model model) {
    	List<User>userList = userService.findAll();
    	Paginated<User>paginated = Util.pagenation(userList, null);
    	model.addAttribute("userList", paginated.getList());
    	model.addAttribute("pageNum", paginated.getPageNum());
    	model.addAttribute("totalPages", paginated.getTotalPages());
		return "user";
    }
	
	@GetMapping(value="/api/user",params="sort",produces ="application/json; charset=UTF-8")
	@ResponseBody
    public ResponseEntity<Object> sort(@RequestParam String page,@RequestParam String sort) {
		Paginated<User> userList = userService.sort(page, sort);
		ResponseData<User> response = new ResponseData<>(null,userList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/user",params="search",produces ="application/json; charset=UTF-8")
	@ResponseBody
    public ResponseEntity<Object> search(@RequestParam String page,@RequestParam String search) {
		Paginated<User> userList = userService.search(page, search);
		ResponseData<User> response = new ResponseData<>(null,userList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/user",params="filter",produces ="application/json; charset=UTF-8")
	@ResponseBody
    public ResponseEntity<Object> filter(@RequestParam String page,@RequestParam String filter) {
		Paginated<User> userList = userService.filter(page, filter);
		ResponseData<User> response = new ResponseData<>(null,userList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
    @GetMapping(value="/api/user",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> selectUser(
    		@RequestParam String page,
    		@RequestParam(name="operation",required=false) String actionParam,
    		@RequestParam(name="check",required=false) String checkedId){
    	Combi<Paginated<User>> combi = userService.selectUser(page, actionParam, checkedId);
    	String nextForm = combi.getKey();
		Paginated<User>userList = combi.getData();
		ResponseData<User> response = new ResponseData<>(null,userList,nextForm,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PostMapping(value="/api/user",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> register(Model model,
    		@Valid @RequestBody User user) {
    	user = userService.register(user);
    	ResponseData<User> response = new ResponseData<>(user,null,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
	@SuppressWarnings("unchecked")
	@PostMapping(value="/api/user/batch",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> csvRegister(Model model,HttpSession session) {
    	Paginated<User> pages = (Paginated<User>)session.getAttribute("csvList");
		pages = userService.registerMultiple(pages.getList());
		session.removeAttribute("csvList");
    	ResponseData<User> response = new ResponseData<>(null,pages,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
    
    @PutMapping(value="/api/user/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> update(Model model,
    		@Valid @RequestBody User user,
    		@PathVariable("id") String checkedId) {
    	return ResponseEntity.status(HttpStatus.OK).body(userService.update(checkedId,user));
    	
    }
    
    @DeleteMapping(value="/api/user/{id}")
    @ResponseBody
    public ResponseEntity<Object> delete(Model model, @PathVariable("id") String checkedId) {	
    	userService.delete(checkedId);
    	return ResponseEntity.noContent().build();
	}
}