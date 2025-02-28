package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.CanalDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ICanalService {
  ResponseEntity<ResponseDTO> saveCanal(CanalDTO CanalDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findCanalById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteCanal(Integer Id);
  
  ResponseEntity<ResponseDTO> updateCanal(Integer Id, CanalDTO CanalDTO);
}

