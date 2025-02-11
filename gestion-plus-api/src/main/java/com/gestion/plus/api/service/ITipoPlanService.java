package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPlanDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoPlanService {
  ResponseEntity<ResponseDTO> saveTipoPlan(TipoPlanDTO paramTipoPlanDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoPlanById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteTipoPlan(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateTipoPlan(Integer paramInteger, TipoPlanDTO paramTipoPlanDTO);
}