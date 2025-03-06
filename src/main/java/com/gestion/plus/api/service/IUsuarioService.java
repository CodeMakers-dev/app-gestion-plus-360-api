package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;

public interface IUsuarioService {
	
	ResponseEntity<ResponseDTO> saveUsuario(UsuarioDTO usuarioDTO);
	
	ResponseEntity<ResponseDTO> updatePassword(UsuarioDTO usuarioDTO);
	
	ResponseEntity<ResponseDTO> findUsuarioById(Integer Id);

}
