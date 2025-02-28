package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.ITipoDocumentoService;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoDocumentoDTO;
import com.gestion.plus.commons.entities.TipoDocumentoEntity;
import com.gestion.plus.commons.maps.TipoDocumentoMapper;
import com.gestion.plus.commons.repositories.TipoDocumentoRepository;
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
public class TipoDocumentoServiceImpl implements ITipoDocumentoService {

	private final TipoDocumentoRepository tipoDocumentoRepository;

	public ResponseEntity<ResponseDTO> saveTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
		log.info("Inicio mguardar tipo documento");
		try {
			TipoDocumentoEntity tipoDocumentoEntity = TipoDocumentoMapper.INSTANCE.dtoToEntity(tipoDocumentoDTO);
			tipoDocumentoEntity.setFechaCreacion(new Date());
			tipoDocumentoEntity.setActivo(Boolean.valueOf(true));
			TipoDocumentoEntity savedEntity = (TipoDocumentoEntity) this.tipoDocumentoRepository
					.save(tipoDocumentoEntity);
			TipoDocumentoDTO savedDTO = TipoDocumentoMapper.INSTANCE.entityToDto(savedEntity);
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
		log.info("Inicio mObtener todos tipos de documentos");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(TipoDocumentoMapper.INSTANCE.beanListToDtoList(this.tipoDocumentoRepository.findAll()))
					.build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.CONSULTING_ERROR).code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findTipoDocumentoById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener los tipos de documento por id");
		Optional<TipoDocumentoEntity> tipoDocumentoOptional = this.tipoDocumentoRepository.findById(id);
		if (tipoDocumentoOptional.isPresent()) {
			TipoDocumentoDTO tipoDocumentoDTO = TipoDocumentoMapper.INSTANCE.entityToDto(tipoDocumentoOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(tipoDocumentoDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("tipo de documento no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateTipoDocumento(Integer id, TipoDocumentoDTO tipoDocumentoDTO) {
		ResponseDTO responseDTO;
		TipoDocumentoEntity existingTipoDocumento = this.tipoDocumentoRepository.findById(id).orElse(null);
		if (existingTipoDocumento != null) {
			existingTipoDocumento.setNombre((tipoDocumentoDTO.getNombre() != null) ? tipoDocumentoDTO.getNombre()
					: existingTipoDocumento.getNombre());
			existingTipoDocumento
					.setDescripcion((tipoDocumentoDTO.getDescripcion() != null) ? tipoDocumentoDTO.getDescripcion()
							: existingTipoDocumento.getNombre());
			existingTipoDocumento.setActivo((tipoDocumentoDTO.getActivo() != null) ? tipoDocumentoDTO.getActivo()
					: existingTipoDocumento.getActivo());
			TipoDocumentoDTO tipoDocumentoDTOR = TipoDocumentoMapper.INSTANCE
					.entityToDto((TipoDocumentoEntity) this.tipoDocumentoRepository.save(existingTipoDocumento));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.UPDATED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(tipoDocumentoDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteTipoDocumento(Integer id) {
		try {
			log.info("Inicio metodo eliminar tipo de documento por id");
			this.tipoDocumentoRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Tipo de documento no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}