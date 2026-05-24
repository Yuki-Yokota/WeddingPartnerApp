package com.example.weddingpartnerapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weddingpartnerapp.common.PDFUtil;
import com.example.weddingpartnerapp.model.Contract;
import com.example.weddingpartnerapp.service.ContractService;

@Controller
public class ContractController {
	
	@Autowired
	private ContractService contractService;
	
	@Autowired
	private PDFUtil pdf;
	
	@GetMapping("/contract")
    public String init(Model model,HttpSession session) {
		
		List<Integer>dishAmountList = contractService.getDishAmountList();
		List<Integer>drinkAmountList = contractService.getDrinkAmountList();
		List<String>venueNameList = contractService.getVenueNameList();
		List<Integer>groomDressAmountList = contractService.getGroomDressAmountList();
		List<Integer>brideDressAmountList = contractService.getBrideDressAmountList();
		
		//表示データがないとホームへ戻る
		if(dishAmountList==null||drinkAmountList==null||venueNameList==null||groomDressAmountList==null||
			brideDressAmountList==null) {
			return "redirect:/home";
		}
		
		model.addAttribute("dishAmountList", dishAmountList);
		model.addAttribute("drinkAmountList", drinkAmountList);
		model.addAttribute("groomDressAmountList", groomDressAmountList);
		model.addAttribute("brideDressAmountList", brideDressAmountList);
		model.addAttribute("venueNameList", venueNameList);
		return "contract";
    }
	
	@GetMapping(value="/api/contract",params="venueName")
	@ResponseBody
    public ResponseEntity<Object> weddingType(@RequestParam String venueName) {
		List<String>weddingTypeList = contractService.getWeddingTypeList(venueName);
		return ResponseEntity.status(HttpStatus.OK).body(weddingTypeList);
	}
	
	@GetMapping(value="/api/contract/pdf",params="id")
	@ResponseBody
    public ResponseEntity<Object> pdf(@RequestParam String id,@RequestParam String funcname) {
		byte[] file = contractService.convertToPDF(id,funcname);
		
        HttpHeaders headers = new HttpHeaders();
        String pdfName = pdf.getTimeStamp(funcname);
		pdf.addContentDisposition(headers, pdfName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
	}
	
	@PostMapping(value="/api/contract",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> register(Model model,HttpSession session,
    		@Valid @RequestBody Contract contract) {
		String customerId = (String)session.getAttribute("selectCustomerId");
		return ResponseEntity.status(HttpStatus.OK).body(contractService.registerContract(contract,customerId));
    }
	
	@PutMapping(value="/api/contract/{id}",produces ="application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Object> contract(Model model,@PathVariable("id") String checkedId) {
		contractService.contract(checkedId);
    	return ResponseEntity.noContent().build();
    	
    }
}
