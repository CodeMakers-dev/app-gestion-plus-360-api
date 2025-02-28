package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.ParametrosDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IParametrosService {
	
	ResponseEntity<ResponseDTO> saveParametro(ParametrosDTO parametrosDTO);
	
	ResponseEntity<ResponseDTO> findAll();
	  
	ResponseEntity<ResponseDTO> findParametroById(Integer Id);
}
