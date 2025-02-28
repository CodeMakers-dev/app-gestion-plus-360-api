package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.CiudadDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ICiudadService {
  ResponseEntity<ResponseDTO> saveCiudad(CiudadDTO ciudadDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findCiudadById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteCiudad(Integer Id);
  
  ResponseEntity<ResponseDTO> updateCiudad(Integer Id, CiudadDTO ciudadDTO);
}
