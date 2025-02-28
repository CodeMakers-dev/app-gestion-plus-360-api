package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPlanDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoPlanService {
  ResponseEntity<ResponseDTO> saveTipoPlan(TipoPlanDTO tipoPlanDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoPlanById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteTipoPlan(Integer Id);
  
  ResponseEntity<ResponseDTO> updateTipoPlan(Integer Id, TipoPlanDTO tipoPlanDTO);
}