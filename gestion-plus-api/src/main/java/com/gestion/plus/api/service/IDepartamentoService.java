package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.DepartamentoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IDepartamentoService {
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findDepartamentoById(Integer Id);
  
  ResponseEntity<ResponseDTO> saveDepartamento(DepartamentoDTO departamentoDTO);
  
  ResponseEntity<ResponseDTO> deleteDepartamento(Integer Id);
  
  ResponseEntity<ResponseDTO> updateDepartamento(Integer Id, DepartamentoDTO departamentoDTO);
}
