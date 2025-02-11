package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.PaisDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IPaisService {
  ResponseEntity<ResponseDTO> savePais(PaisDTO paramPaisDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findPaisById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deletePais(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updatePais(Integer paramInteger, PaisDTO paramPaisDTO);
}
