package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPersonaDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoPersonaService {
  ResponseEntity<ResponseDTO> saveTipoPersona(TipoPersonaDTO tipoPersonaDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoPersonaById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteTipoPersona(Integer Id);
  
  ResponseEntity<ResponseDTO> updateTipoPersona(Integer Id, TipoPersonaDTO tipoPersonaDTO);
}
