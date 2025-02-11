package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.DepartamentoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IDepartamentoService {
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findDepartamentoById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> saveDepartamento(DepartamentoDTO paramDepartamentoDTO);
  
  ResponseEntity<ResponseDTO> deleteDepartamento(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateDepartamento(Integer paramInteger, DepartamentoDTO paramDepartamentoDTO);
}
