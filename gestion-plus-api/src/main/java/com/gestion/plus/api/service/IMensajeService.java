package com.gestion.plus.api.service;

import com.gestion.plus.commons.dtos.MensajeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface IMensajeService {
	
	ResponseEntity<ResponseDTO> sendMensaje(MensajeDTO mensajeDTO);
	
	ResponseEntity<ResponseDTO> sendMensajeAll(MensajeDTO mensajeDTO);

    ResponseEntity<ResponseDTO> findMensajeById(Integer idUsuario);

    ResponseEntity<ResponseDTO> findAll();
}
