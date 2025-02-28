package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.IPaisService;

import com.gestion.plus.commons.dtos.PaisDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.PaisEntity;
import com.gestion.plus.commons.maps.PaisMapper;
import com.gestion.plus.commons.repositories.PaisRepository;
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
public class PaisServiceImpl implements IPaisService {

	private final PaisRepository paisRepository;

	public ResponseEntity<ResponseDTO> savePais(PaisDTO paisDTO) {
		log.info("Inicio mguardar pais");
		try {
			PaisEntity paisEntity = PaisMapper.INSTANCE.dtoToEntity(paisDTO);
			paisEntity.setFechaCreacion(new Date());
			paisEntity.setActivo(Boolean.valueOf(true));
			PaisEntity savedEntity = (PaisEntity) this.paisRepository.save(paisEntity);
			PaisDTO savedDTO = PaisMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin mguardar pais");
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
		log.info("Inicio mObtener todos los paises");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(PaisMapper.INSTANCE.beanListToDtoList(this.paisRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findPaisById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener pais por id");
		Optional<PaisEntity> paisOptional = this.paisRepository.findById(id);
		if (paisOptional.isPresent()) {
			PaisDTO paisDTO = PaisMapper.INSTANCE.entityToDto(paisOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(paisDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("pais no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updatePais(Integer id, PaisDTO paisDTO) {
		ResponseDTO responseDTO;
		PaisEntity existingPais = this.paisRepository.findById(id).orElse(null);
		if (existingPais != null) {
			existingPais.setNombre((paisDTO.getNombre() != null) ? paisDTO.getNombre() : existingPais.getNombre());
			existingPais
					.setCodigoIso((paisDTO.getCodigoIso() != null) ? paisDTO.getCodigoIso() : existingPais.getNombre());
			existingPais.setActivo((paisDTO.getActivo() != null) ? paisDTO.getActivo() : existingPais.getActivo());
			PaisDTO paisDTOR = PaisMapper.INSTANCE.entityToDto((PaisEntity) this.paisRepository.save(existingPais));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(paisDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deletePais(Integer id) {
		try {
			log.info("Inicio metodo eliminar pais por id");
			this.paisRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("pais no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
