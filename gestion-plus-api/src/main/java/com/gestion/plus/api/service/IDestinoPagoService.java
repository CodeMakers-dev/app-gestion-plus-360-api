package com.gestion.plus.api.service;


import com.gestion.plus.commons.dtos.DestinoPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IDestinoPagoService {
  ResponseEntity<ResponseDTO> saveDestinoPago(DestinoPagoDTO paramDestinoPagoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findDestinoPagoById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteDestinoPago(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateDestinoPago(Integer paramInteger, DestinoPagoDTO paramDestinoPagoDTO);
}