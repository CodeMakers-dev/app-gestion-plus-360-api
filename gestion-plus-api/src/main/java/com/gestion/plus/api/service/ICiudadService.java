package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.CiudadDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ICiudadService {
  ResponseEntity<ResponseDTO> saveCiudad(CiudadDTO paramCiudadDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findCiudadById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteCiudad(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateCiudad(Integer paramInteger, CiudadDTO paramCiudadDTO);
}
