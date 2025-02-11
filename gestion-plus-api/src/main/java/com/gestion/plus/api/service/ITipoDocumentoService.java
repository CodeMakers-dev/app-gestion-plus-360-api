package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoDocumentoDTO;
import org.springframework.http.ResponseEntity;

public interface ITipoDocumentoService {
  ResponseEntity<ResponseDTO> saveTipoDocumento(TipoDocumentoDTO paramTipoDocumentoDTO);
  
  ResponseEntity<ResponseDTO> findAll();
  
  ResponseEntity<ResponseDTO> findTipoDocumentoById(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> deleteTipoDocumento(Integer paramInteger);
  
  ResponseEntity<ResponseDTO> updateTipoDocumento(Integer paramInteger, TipoDocumentoDTO paramTipoDocumentoDTO);
}
