package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.ICanalService;


import com.gestion.plus.commons.dtos.CanalDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.CanalEntity;
import com.gestion.plus.commons.maps.CanalMapper;
import com.gestion.plus.commons.repositories.CanalRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CanalServiceImpl implements ICanalService {

	private final CanalRepository canalRepository;

	public ResponseEntity<ResponseDTO> saveCanal(CanalDTO canalDTO) {
		log.info("Inicio metodo guardar canal");
		try {
			CanalEntity canalEntity = CanalMapper.INSTANCE.dtoToEntity(canalDTO);
			canalEntity.setFechaCreacion(new Date());
			canalEntity.setActivo(Boolean.valueOf(true));
			CanalEntity savedEntity = (CanalEntity) this.canalRepository.save(canalEntity);
			CanalDTO savedDTO = CanalMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin metodo guardar canal");
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
		log.info("Inicio metodo Obtener todos los canales");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(CanalMapper.INSTANCE.beanListToDtoList(this.canalRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findCanalById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener los tipos de canales por id");
		Optional<CanalEntity> canalOptional = this.canalRepository.findById(id);
		if (canalOptional.isPresent()) {
			CanalDTO canalDTO = CanalMapper.INSTANCE.entityToDto(canalOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(canalDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("canal no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateCanal(Integer id, CanalDTO canalDTO) {
		ResponseDTO responseDTO;
		CanalEntity existingCanal = this.canalRepository.findById(id).orElse(null);
		if (existingCanal != null) {
			existingCanal.setNombre((canalDTO.getNombre() != null) ? 
				 canalDTO.getNombre() : existingCanal.getNombre());
			existingCanal.setDescripcion(
					(canalDTO.getDescripcion() != null) ? 
					canalDTO.getDescripcion() : existingCanal.getNombre());
			existingCanal.setActivo((canalDTO.getActivo() != null) ? canalDTO.getActivo() : existingCanal.getActivo());
			CanalDTO canalDTOR = CanalMapper.INSTANCE
					.entityToDto((CanalEntity) this.canalRepository.save(existingCanal));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(canalDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteCanal(Integer id) {
		try {
			log.info("Inicio metodo eliminar canal por id");
			this.canalRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("canal no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}