package com.gestion.plus.api.services.impl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IUsuarioRolService;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioRolDTO;
import com.gestion.plus.commons.entities.UsuarioRolEntity;
import com.gestion.plus.commons.maps.UsuarioRolMapper;
import com.gestion.plus.commons.repositories.UsuarioRolRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioRolServiceImpl implements IUsuarioRolService {

	private final UsuarioRolRepository usuarioRolRepository;
	
	public ResponseEntity<ResponseDTO> findUsuarioRolById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los Usuario Rol por id");
		Optional<UsuarioRolEntity> usuarioRolOptional = this.usuarioRolRepository.findById(id);
		if (usuarioRolOptional.isPresent()) {
			UsuarioRolDTO usuarioRolDTO = UsuarioRolMapper.INSTANCE.entityToDto(usuarioRolOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(usuarioRolDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("usuario rol no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}
	
	public ResponseEntity<ResponseDTO> findUsuarioRolByUserId(Integer userId) {
	    ResponseDTO responseDTO;
	    log.info("Inicio del metodo para obtener UsuarioRol por userId: {}", userId);
	    Optional<UsuarioRolEntity> usuarioRolOptional = usuarioRolRepository.findByUsuarioId(userId);
	    if (usuarioRolOptional.isPresent()) {
	        UsuarioRolDTO usuarioRolDTO = UsuarioRolMapper.INSTANCE.entityToDto(usuarioRolOptional.get());
	        responseDTO = ResponseDTO.builder()
	                .success(true)
	                .message(ResponseMessages.CONSULTED_SUCCESSFULLY)
	                .code(HttpStatus.OK.value())
	                .response(usuarioRolDTO)
	                .build();
	    } else {
	        responseDTO = ResponseDTO.builder()
	                .code(HttpStatus.NOT_FOUND.value())
	                .message("UsuarioRol no encontrado para el userId: " + userId)
	                .response(null)
	                .build();
	    }
	    return ResponseEntity.status(responseDTO.getCode()).body(responseDTO);
	}
}
