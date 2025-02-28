package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.OrigenPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IOrigenPagoService {
  ResponseEntity<ResponseDTO> saveOrigenPago(OrigenPagoDTO origenPagoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findOrigenPagoById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteOrigenPago(Integer Id);
  
  ResponseEntity<ResponseDTO> updateOrigenPago(Integer Id, OrigenPagoDTO origenPagoDTO);
}