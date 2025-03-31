package com.gestion.plus.api.services.impl;

import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IRolService;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.RolDTO;
import com.gestion.plus.commons.entities.RolEntity;
import com.gestion.plus.commons.maps.RolMapper;
import com.gestion.plus.commons.repositories.RolRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolServiceImpl implements IRolService{
	
	private final RolRepository rolRepository;
	
	public ResponseEntity<ResponseDTO> findRolById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los tipos de rol por id");
		Optional<RolEntity> rolOptional = this.rolRepository.findById(id);
		if (rolOptional.isPresent()) {
			RolDTO rolDTO = RolMapper.INSTANCE.entityToDto(rolOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(rolDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("rol no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}
	
	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo Obtener todos los roles");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(RolMapper.INSTANCE.beanListToDtoList(this.rolRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}
}
