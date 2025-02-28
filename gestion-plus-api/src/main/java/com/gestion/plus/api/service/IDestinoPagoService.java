package com.gestion.plus.api.service;


import com.gestion.plus.commons.dtos.DestinoPagoDTO;

import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IDestinoPagoService {
  ResponseEntity<ResponseDTO> saveDestinoPago(DestinoPagoDTO destinoPagoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findDestinoPagoById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteDestinoPago(Integer Id);
  
  ResponseEntity<ResponseDTO> updateDestinoPago(Integer Id, DestinoPagoDTO destinoPagoDTO);
}