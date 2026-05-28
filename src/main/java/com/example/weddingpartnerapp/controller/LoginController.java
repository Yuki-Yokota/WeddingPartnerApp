package com.example.weddingpartnerapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.service.CustomerService;
import com.example.weddingpartnerapp.service.UserService;
import com.example.weddingpartnerapp.service.WeddingService;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService cusService;
	
	@Autowired
	private WeddingService wedService;
	
	@GetMapping("/login")
	public String transit() {
		return "login";
	}
	
    @PostMapping("/login")
    public String authenticate(Model model,
    		@RequestParam String mailAddress,
    		@RequestParam String password,
    		HttpSession session) {
    	User user = new User(null,null,mailAddress,password,null, null);
    	
    	try {
    		user = userService.authenticate(user);
    	}catch(ApplicationException e) {
    		model.addAttribute("error", "予期せぬエラーが発生しています。管理者にご連絡ください");
    		return "login";
    	}
    	if(user!=null) {
    		SessionUser sessionUser = new SessionUser(user.getUserId(),user.getRole());
    		
    		List<CustomerDTO> customerList = cusService.findAllCustomer(sessionUser);
    		List<WeddingDTO> weddingList = wedService.findAll(sessionUser);
        	session.setAttribute("user", sessionUser);
        	model.addAttribute("customerList",customerList);
        	model.addAttribute("weddingList",weddingList);
        	
            return "home";
    	}else {
    		model.addAttribute("error", "メールアドレスかパスワードが間違っています");
            return "login";
    	}
    	
    }
}