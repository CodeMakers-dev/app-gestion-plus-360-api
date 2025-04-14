package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.AsesoresDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IAsesoresService {
	 
	  ResponseEntity<ResponseDTO> saveAsesor(AsesoresDTO asesoresDTO);
	  
	  ResponseEntity<ResponseDTO> findAll();
	  
	  ResponseEntity<ResponseDTO> findAsesorById(Integer Id);
	  
	  ResponseEntity<ResponseDTO> deleteAsesor(Integer Id);
	  
	  //ResponseEntity<ResponseDTO> updateAsesor(Integer Id, AsesoresDTO asesoresDTO);
}
