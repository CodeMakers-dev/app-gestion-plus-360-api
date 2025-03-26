package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IUsuarioRolService {

	ResponseEntity<ResponseDTO> findUsuarioRolById(Integer id);
	
	ResponseEntity<ResponseDTO> findUsuarioRolByUserId(Integer id);
	
}
