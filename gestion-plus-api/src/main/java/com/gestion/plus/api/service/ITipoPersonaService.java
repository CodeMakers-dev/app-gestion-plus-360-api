package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPersonaDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoPersonaService {
  ResponseEntity<ResponseDTO> saveTipoPersona(TipoPersonaDTO paramTipoPersonaDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoPersonaById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteTipoPersona(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateTipoPersona(Integer paramInteger, TipoPersonaDTO paramTipoPersonaDTO);
}
