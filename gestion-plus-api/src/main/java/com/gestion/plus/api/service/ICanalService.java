package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.CanalDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ICanalService {
  ResponseEntity<ResponseDTO> saveCanal(CanalDTO paramCanalDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findCanalById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteCanal(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateCanal(Integer paramInteger, CanalDTO paramCanalDTO);
}

