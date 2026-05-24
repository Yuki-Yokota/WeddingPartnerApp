package com.example.weddingpartnerapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.weddingpartnerapp.common.HomeAction;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.service.CustomerService;
import com.example.weddingpartnerapp.service.WeddingService;

@Controller
public class HomeController {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private WeddingService weddingService;
	
	@GetMapping("/home")
	public String transitHome(){
		return "home";
	}
	
    @PostMapping("/home")
    public String homeSelect(Model model,
    		HttpSession session,
    		@RequestParam(name="action",required=false) String action,
    		@RequestParam(name="selectCustomerId",required=false) String selectCustomerId,
    		@RequestParam(name="selectWeddingId",required=false) String selectWeddingId) {
    	SessionUser user = (SessionUser)session.getAttribute("user");
    	HomeAction homeAction = HomeAction.form(action);
    	switch(homeAction) {
		case CUSTOMER:
			return "redirect:/customer";
		case CONTRACT:
			session.setAttribute("selectCustomerId", selectCustomerId);
			return "redirect:/contract";
		case WEDDING:
			return "redirect:/wedding";
		case GUEST:
			session.setAttribute("selectWeddingId", selectWeddingId);
			return "redirect:/guest";
		case USER:
			return "redirect:/user";
		default:
			List<CustomerDTO>customerList = customerService.findAllCustomer(user);
			List<WeddingDTO>weddingList = weddingService.findAll(user);
			model.addAttribute("customerList",customerList);
			model.addAttribute("weddingList",weddingList);
			return "home";
		}
		
    }
}