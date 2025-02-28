package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.ITipoPersonaService;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPersonaDTO;
import com.gestion.plus.commons.entities.TipoPersonaEntity;
import com.gestion.plus.commons.maps.TipoPersonaMapper;
import com.gestion.plus.commons.repositories.TipoPersonaRepository;
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
public class TipoPersonaServiceImpl implements ITipoPersonaService {

	private final TipoPersonaRepository tipoPersonaRepository;

	public ResponseEntity<ResponseDTO> saveTipoPersona(TipoPersonaDTO tipoPersonaDTO) {
		log.info("Inicio mguardar tipo persona");
		try {
			TipoPersonaEntity tipoPersonaEntity = TipoPersonaMapper.INSTANCE.dtoToEntity(tipoPersonaDTO);
			tipoPersonaEntity.setFechaCreacion(new Date());
			tipoPersonaEntity.setActivo(Boolean.valueOf(true));
			TipoPersonaEntity savedEntity = (TipoPersonaEntity) this.tipoPersonaRepository.save(tipoPersonaEntity);
			TipoPersonaDTO savedDTO = TipoPersonaMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin mguardar tipo documento");
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
		log.info("Inicio mObtener todos  los tipos persona");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(TipoPersonaMapper.INSTANCE.beanListToDtoList(this.tipoPersonaRepository.findAll()))
					.build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findTipoPersonaById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener los tipos persona por id");
		Optional<TipoPersonaEntity> tipoPersonaOptional = this.tipoPersonaRepository.findById(id);
		if (tipoPersonaOptional.isPresent()) {
			TipoPersonaDTO tipoPersonaDTO = TipoPersonaMapper.INSTANCE.entityToDto(tipoPersonaOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(tipoPersonaDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("tipo persona no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateTipoPersona(Integer id, TipoPersonaDTO tipoPersonaDTO) {
		ResponseDTO responseDTO;
		TipoPersonaEntity existingTipoPersona = this.tipoPersonaRepository.findById(id).orElse(null);
		if (existingTipoPersona != null) {
			existingTipoPersona.setNombre((tipoPersonaDTO.getNombre() != null) ? tipoPersonaDTO.getNombre()
					: existingTipoPersona.getNombre());
			existingTipoPersona
					.setDescripcion((tipoPersonaDTO.getDescripcion() != null) ? tipoPersonaDTO.getDescripcion()
							: existingTipoPersona.getNombre());
			existingTipoPersona.setActivo((tipoPersonaDTO.getActivo() != null) ? tipoPersonaDTO.getActivo()
					: existingTipoPersona.getActivo());
			TipoPersonaDTO tipoPersonaDTOR = TipoPersonaMapper.INSTANCE
					.entityToDto((TipoPersonaEntity) this.tipoPersonaRepository.save(existingTipoPersona));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(tipoPersonaDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteTipoPersona(Integer id) {
		try {
			log.info("Inicio metodo eliminar tipo persona por id");
			this.tipoPersonaRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Tipo persona no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
