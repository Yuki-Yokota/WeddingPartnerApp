package com.example.weddingpartnerapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.CustomerDTO;
import com.example.weddingpartnerapp.model.GuestDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.ResponseData;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.service.CustomerService;
import com.example.weddingpartnerapp.service.GuestService;
import com.example.weddingpartnerapp.service.UserService;
import com.example.weddingpartnerapp.service.WeddingService;

@RestController
public class CSVController{
	@Autowired
	private UserService userService;
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private WeddingService weddingService;
	
	@Autowired
	private GuestService guestService;
	
	/**
	 * ユーザCSVダウンロード窓口
	 * 1.ヘッダ取得、設定
	 * 2.csvファイル取得、返す
	 * @return
	 */
	@GetMapping(value="/csv/user")
    public ResponseEntity<Object> downloadUser() {
		HttpHeaders headers = new HttpHeaders();
		String csvFileName = CSVDownload.getCSVFileName("user");
		CSVDownload.addContentDisposition(headers, csvFileName);
        return new ResponseEntity<>(userService.exportCsv(), headers, HttpStatus.OK);
	}
	
	@GetMapping(value="/csv/customer")
    public ResponseEntity<Object> downloadCustomer(HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		HttpHeaders headers = new HttpHeaders();
		String csvFileName = CSVDownload.getCSVFileName("customer");
		CSVDownload.addContentDisposition(headers, csvFileName);
        return new ResponseEntity<>(customerService.exportCsv(user), headers, HttpStatus.OK);
	}
	
	@GetMapping(value="/csv/wedding")
    public ResponseEntity<Object> downloadWedding(HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		HttpHeaders headers = new HttpHeaders();
		String csvFileName = CSVDownload.getCSVFileName("wedding");
		CSVDownload.addContentDisposition(headers, csvFileName);
        return new ResponseEntity<>(weddingService.exportCsv(user), headers, HttpStatus.OK);
	}
	
	@GetMapping(value="/csv/guest")
    public ResponseEntity<Object> downloadGuest(HttpSession session) {
		String selectWeddingId = (String)session.getAttribute("selectWeddingId");
		HttpHeaders headers = new HttpHeaders();
		String csvFileName = CSVDownload.getCSVFileName("guest");
		CSVDownload.addContentDisposition(headers, csvFileName);
        return new ResponseEntity<>(guestService.exportCsv(selectWeddingId), headers, HttpStatus.OK);
	}
	
	@PostMapping(value="/csv/user")
    public ResponseEntity<Object> uploadUser(@RequestParam("csvfile") MultipartFile file,HttpSession session) {
		Combi<List<User>>combi = userService.importCsv(file);
		Integer csvUploadLim=Integer.parseInt(combi.getKey());
		Paginated<User> pages = Util.pagenation(combi.getData(), null);
		session.setAttribute("csvList", pages);
		ResponseData<User> response = new ResponseData<>(null,pages,null,csvUploadLim);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value="/csv/customer")
    public ResponseEntity<Object> uploadCustomer(@RequestParam("csvfile") MultipartFile file,HttpSession session) {
		SessionUser user = (SessionUser)session.getAttribute("user");
		Combi<List<CustomerDTO>>combi = customerService.importCsv(file,user);
		Integer csvUploadLim=Integer.parseInt(combi.getKey());
		Paginated<CustomerDTO> pages = Util.pagenation(combi.getData(), null);
		session.setAttribute("csvList", pages);
		ResponseData<CustomerDTO> response = new ResponseData<>(null,pages,null,csvUploadLim);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value="/csv/guest")
    public ResponseEntity<Object> uploadGuest(@RequestParam("csvfile") MultipartFile file,HttpSession session) {
		String selectWeddingId = (String)session.getAttribute("selectWeddingId");
		Combi<List<GuestDTO>>combi = guestService.importCsv(file,selectWeddingId);
		Integer csvUploadLim=Integer.parseInt(combi.getKey());
		Paginated<GuestDTO> pages = Util.pagenation(combi.getData(), null);
		session.setAttribute("csvList", pages);
		ResponseData<GuestDTO> response = new ResponseData<>(null,pages,null,csvUploadLim);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
