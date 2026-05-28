package com.example.weddingpartnerapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.model.ErrorCombi;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseEntity<Object> exceptionHandler(ApplicationException e) {
    	List<ErrorCombi>error = new ArrayList<>();
    	error.add(new ErrorCombi("err",e.getMessage()));
        //log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> ValidExceptionHandler(MethodArgumentNotValidException e) {
    	List<ErrorCombi>error = new ArrayList<>();
    	BindingResult result = e.getBindingResult();
    	for(FieldError fe : result.getFieldErrors()) {
			String code=fe.getField();
			code=code.replace("data.", "")+"Valid";
			error.add(new ErrorCombi(code,fe.getDefaultMessage()));
		}
        //log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
   
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Model model,Exception e) {
    	model.addAttribute("error","予期せぬエラーが発生しました。");
        //log.error(e.getMessage(), e);
        return "login";
    }
	
}
