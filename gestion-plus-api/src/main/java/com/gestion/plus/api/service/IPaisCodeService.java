package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.PaisCodeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IPaisCodeService {
	  
	  ResponseEntity<ResponseDTO> savePaisCode(PaisCodeDTO paramPaisCodeDTO);
	  
	  ResponseEntity<ResponseDTO> findAll();
	  
	  ResponseEntity<ResponseDTO> findPaisCodeById(Integer paramInteger);
	  
	  ResponseEntity<ResponseDTO> deletePaisCode(Integer paramInteger);
	  
	  ResponseEntity<ResponseDTO> updatePaisCode(Integer paramInteger, PaisCodeDTO paramPaisCodeDTO);
}
