package com.example.weddingpartnerapp.controller;

import java.util.List;
import java.util.Map;

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
import com.example.weddingpartnerapp.model.GuestDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.service.GuestService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class GuestController {
	
	@Autowired
	private GuestService guestService;
	
	@GetMapping("/guest")
    public String init(Model model,HttpSession session) {
		//セッションで取得予定、インターセプターでguest以外は削除する
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
    	List<GuestDTO>guestList = guestService.findAll(selectWeddingId);
    	Paginated<GuestDTO>paginated = Util.pagenation(guestList, null);
    	model.addAttribute("guestList", paginated.getList());
    	model.addAttribute("pageNum", paginated.getPageNum());
    	model.addAttribute("totalPages", paginated.getTotalPages());
		return "guest";
    }
	
	@GetMapping(value="/api/guest",params="sort")
	@ResponseBody
    public ResponseEntity<Object> sort(@RequestParam String page,@RequestParam String sort,HttpSession session) {
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
		Paginated<GuestDTO> guestList = guestService.sort(page, sort,selectWeddingId);
		ResponseData<GuestDTO> response = new ResponseData<>(null,guestList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/guest",params="search")
	@ResponseBody
    public ResponseEntity<Object> search(@RequestParam String page,@RequestParam String search,HttpSession session) {
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
		Paginated<GuestDTO> guestList = guestService.search(page, search,selectWeddingId);
		ResponseData<GuestDTO> response = new ResponseData<>(null,guestList,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/api/guest",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> selectGuest(
    		@RequestParam String page,
    		HttpSession session,
    		@RequestParam(name="operation",required=false) String actionParam,
    		@RequestParam(name="check",required=false) String checkedId){
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
    	Combi<Paginated<GuestDTO>> combi = guestService.selectGuest(page, actionParam, checkedId,selectWeddingId);
    	String nextForm = combi.getKey();
		Paginated<GuestDTO>guestList = combi.getData();
		ResponseData<GuestDTO> response = new ResponseData<>(null,guestList,nextForm,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@PostMapping(value="/api/guest",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> register(Model model,HttpSession session,
    		@Valid @RequestBody GuestDTO guest) {
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
    	guest = guestService.register(guest,selectWeddingId);
    	ResponseData<GuestDTO> response = new ResponseData<>(guest,null,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	
	@SuppressWarnings("unchecked")
	@PostMapping(value="/api/guest/batch",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> csvRegister(Model model,HttpSession session) {
		Paginated<GuestDTO> pages = (Paginated<GuestDTO>)session.getAttribute("csvList");
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
		pages = guestService.registerMultiple(pages.getList(),selectWeddingId);
		session.removeAttribute("csvList");
    	ResponseData<GuestDTO> response = new ResponseData<>(null,pages,null,null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value="/api/guest/mail",produces ="application/json; charset=UTF-8")
	@ResponseBody
    public ResponseEntity<Object> mailDistribution(HttpSession session,
    		@RequestBody Map<String,String> requestData) {
		String guestId = requestData.get("guestId");
		SessionUser user = (SessionUser)session.getAttribute("user");
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
		guestService.mailDistribution(guestId, user,selectWeddingId);
		
		ResponseData<GuestDTO> response = new ResponseData<>(null,null,"mailDistributionClose",null);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping(value="/api/guest/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> update(Model model,
    		HttpSession session,
    		@Valid @RequestBody GuestDTO guest,
    		@PathVariable("id") String checkedId) {
		String selectWeddingId = (String) session.getAttribute("selectWeddingId");
    	return ResponseEntity.status(HttpStatus.OK).body(guestService.update(checkedId,selectWeddingId,guest));
    	
    }
    
    @DeleteMapping(value="/api/guest/{id}")
    @ResponseBody
    public ResponseEntity<Object> delete(Model model, @PathVariable("id") String checkedId) {	
    	guestService.delete(checkedId);
    	return ResponseEntity.noContent().build();
	}
}
