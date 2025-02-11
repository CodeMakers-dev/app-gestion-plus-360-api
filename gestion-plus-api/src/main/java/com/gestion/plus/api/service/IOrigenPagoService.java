package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.OrigenPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IOrigenPagoService {
  ResponseEntity<ResponseDTO> saveOrigenPago(OrigenPagoDTO paramOrigenPagoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findOrigenPagoById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteOrigenPago(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateOrigenPago(Integer paramInteger, OrigenPagoDTO paramOrigenPagoDTO);
}