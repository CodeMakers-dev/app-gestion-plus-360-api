package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.PaisDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IPaisService {
  ResponseEntity<ResponseDTO> savePais(PaisDTO paisDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findPaisById(Integer Id);
  
  ResponseEntity<ResponseDTO> deletePais(Integer Id);
  
  ResponseEntity<ResponseDTO> updatePais(Integer Id, PaisDTO paisDTO);
}
