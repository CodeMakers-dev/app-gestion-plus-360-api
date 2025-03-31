package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IRolService {
	
	  ResponseEntity<ResponseDTO> findRolById(Integer Id);
	
	  ResponseEntity<ResponseDTO> findAll();
}
