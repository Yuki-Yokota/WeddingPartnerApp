package com.example.weddingpartnerapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weddingpartnerapp.model.GuestDTO;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.service.GuestService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AllergyQuestionnaireForm {
	
	@Autowired
	private GuestService guestService;
	
	@GetMapping(value="/questionnaire")
	public String init(Model model,@RequestParam String id) {
		model.addAttribute("guestId",id);
		return "allergyquestionnaireform";
	}
	
	@PostMapping(value="/questionnaire",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> registerAllergy(Model model,
    		@RequestBody GuestDTO guest,
    		HttpSession session) {
		guestService.registerAllergy(guest);
		
		//戻り値は改修予定
		ResponseData<GuestDTO> response = new ResponseData<>(guest,null,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
