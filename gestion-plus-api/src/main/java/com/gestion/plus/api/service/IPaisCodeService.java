package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.PaisCodeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IPaisCodeService {
	  
	  ResponseEntity<ResponseDTO> savePaisCode(PaisCodeDTO paisCodeDTO);
	  
	  ResponseEntity<ResponseDTO> findAll();
	  
	  ResponseEntity<ResponseDTO> findPaisCodeById(Integer Id);
	  
	  ResponseEntity<ResponseDTO> deletePaisCode(Integer Id);
	  
	  ResponseEntity<ResponseDTO> updatePaisCode(Integer Id, PaisCodeDTO paisCodeDTO);
}
