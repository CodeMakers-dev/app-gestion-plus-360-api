package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoDocumentoDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoDocumentoService {
  ResponseEntity<ResponseDTO> saveTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoDocumentoById(Integer Id);
  
  ResponseEntity<ResponseDTO> deleteTipoDocumento(Integer Id);
  
  ResponseEntity<ResponseDTO> updateTipoDocumento(Integer Id, TipoDocumentoDTO tipoDocumentoDTO);
}
