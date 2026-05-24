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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.service.WeddingService;

@Controller
public class WeddingController {
	@Autowired
	private WeddingService weddingService;
	
	@GetMapping("/wedding")
    public String init(Model model,HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
    	List<WeddingDTO>weddingList = weddingService.findAll(user);
    	Paginated<WeddingDTO>paginated = Util.pagenation(weddingList, null);
    	model.addAttribute("weddingList", paginated.getList());
    	model.addAttribute("pageNum", paginated.getPageNum());
    	model.addAttribute("totalPages", paginated.getTotalPages());
		return "wedding";
    }
	
	@GetMapping(value="/api/wedding",params="sort")
	@ResponseBody
    public ResponseEntity<Object> sort(@RequestParam String page,@RequestParam String sort,HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Paginated<WeddingDTO> weddingList = weddingService.sort(page, sort,user);
		ResponseData<WeddingDTO> response = new ResponseData<>(null,weddingList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/wedding",params="search")
	@ResponseBody
    public ResponseEntity<Object> search(@RequestParam String page,@RequestParam String search,HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Paginated<WeddingDTO> weddingList = weddingService.search(page, search,user);
		ResponseData<WeddingDTO> response = new ResponseData<>(null,weddingList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/wedding",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> selectwedding(
    		@RequestParam String page,
    		@RequestParam(name="operation",required=false) String actionParam,
    		@RequestParam(name="check",required=false) String checkedId,
    		HttpSession session){
		SessionUser user = (SessionUser)session.getAttribute("user");
    	Combi<Paginated<WeddingDTO>> combi = weddingService.selectWedding(page, actionParam, checkedId,user);
    	String nextForm = combi.getKey();
		Paginated<WeddingDTO>weddingList = combi.getData();
		ResponseData<WeddingDTO> response = new ResponseData<>(null,weddingList,nextForm,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@GetMapping(value="/api/wedding/venue",produces ="application/json; charset=UTF-8")
	@ResponseBody
    public ResponseEntity<Object> venue() {
		List<String> venueNameList = weddingService.getVenueNameList();
		Paginated<String>paginated = Util.pagenation(venueNameList, null);
		ResponseData<String> response = new ResponseData<>(null,paginated,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
    
	@PutMapping(value="/api/wedding/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> update(Model model,
    		@Valid @RequestBody WeddingDTO wedding,
    		@PathVariable("id") String checkedId) {
    	return ResponseEntity.status(HttpStatus.OK).body(weddingService.update(checkedId,wedding));
    	
    }
    
    @DeleteMapping(value="/api/wedding/{id}")
    @ResponseBody
    public ResponseEntity<Object> delete(Model model, @PathVariable("id") String checkedId) {	
    	weddingService.delete(checkedId);
    	return ResponseEntity.noContent().build();
	}
}
