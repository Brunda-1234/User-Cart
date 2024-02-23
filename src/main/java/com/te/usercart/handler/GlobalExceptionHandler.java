package com.te.usercart.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.te.usercart.dto.Response;
import com.te.usercart.exception.DataNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {DataNotFoundException.class})
	public ResponseEntity<Response> addHandler(DataNotFoundException e){
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(true,e.getMessage(), null));
	}
}
