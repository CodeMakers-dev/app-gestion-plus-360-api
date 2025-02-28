package com.gestion.plus.api.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.gestion.plus.api.service.IParametrosService;
import com.gestion.plus.commons.dtos.ParametrosDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.ParametrosEntity;
import com.gestion.plus.commons.maps.ParametrosMapper;
import com.gestion.plus.commons.repositories.ParametrosRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametrosServiceImpl implements IParametrosService {

	private final ParametrosRepository parametrosRepository;

	public ResponseEntity<ResponseDTO> saveParametro(ParametrosDTO parametrosDTO) {
		log.info("Inicio metodo guardar Parametro");
		try {
			ParametrosEntity parametrosEntity = ParametrosMapper.INSTANCE.dtoToEntity(parametrosDTO);
			parametrosEntity.setFechaCreacion(new Date());
			parametrosEntity.setActivo(Boolean.valueOf(true));
			ParametrosEntity savedEntity = (ParametrosEntity) this.parametrosRepository.save(parametrosEntity);
			ParametrosDTO savedDTO = ParametrosMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin metodo guardar parametro");
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.SAVED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.CREATED.value()))
					.response(savedDTO).build();
			return ResponseEntity.status((HttpStatusCode) HttpStatus.CREATED).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO errorResponse = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.SAVE_ERROR).code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).build();
			return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo Obtener todos los parametros");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(
					ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(ParametrosMapper.INSTANCE.beanListToDtoList(this.parametrosRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findParametroById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los parametros por id");
		Optional<ParametrosEntity> parametroOptional = this.parametrosRepository.findById(id);
		if (parametroOptional.isPresent()) {
			ParametrosDTO parametrosDTO = ParametrosMapper.INSTANCE.entityToDto(parametroOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(parametrosDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("parametro no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}
}
