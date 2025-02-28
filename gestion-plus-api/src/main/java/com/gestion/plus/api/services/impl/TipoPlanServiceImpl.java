package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.ITipoPlanService;

import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.TipoPlanDTO;
import com.gestion.plus.commons.entities.TipoPlanEntity;
import com.gestion.plus.commons.maps.TipoPlanMapper;
import com.gestion.plus.commons.repositories.TipoPlanRepository;
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
public class TipoPlanServiceImpl implements ITipoPlanService {

	private final TipoPlanRepository tipoPlanRepository;

	public ResponseEntity<ResponseDTO> saveTipoPlan(TipoPlanDTO tipoPlanDTO) {
		log.info("Inicio mguardar tipo plan");
		try {
			TipoPlanEntity tipoPlanEntity = TipoPlanMapper.INSTANCE.dtoToEntity(tipoPlanDTO);
			tipoPlanEntity.setFechaCreacion(new Date());
			tipoPlanEntity.setActivo(Boolean.valueOf(true));
			TipoPlanEntity savedEntity = (TipoPlanEntity) this.tipoPlanRepository.save(tipoPlanEntity);
			TipoPlanDTO savedDTO = TipoPlanMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin mguardar tipo plan");
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
		log.info("Inicio mObtener todos los tipos de plan");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(TipoPlanMapper.INSTANCE.beanListToDtoList(this.tipoPlanRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findTipoPlanById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener los tipos de plan por id");
		Optional<TipoPlanEntity> tipoPlanOptional = this.tipoPlanRepository.findById(id);
		if (tipoPlanOptional.isPresent()) {
			TipoPlanDTO tipoPlanDTO = TipoPlanMapper.INSTANCE.entityToDto(tipoPlanOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(tipoPlanDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("tipo de plan no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateTipoPlan(Integer id, TipoPlanDTO tipoPlanDTO) {
		ResponseDTO responseDTO;
		TipoPlanEntity existingTipoPlan = this.tipoPlanRepository.findById(id).orElse(null);
		if (existingTipoPlan != null) {
			existingTipoPlan.setNombre(
					(tipoPlanDTO.getNombre() != null) ? tipoPlanDTO.getNombre() : existingTipoPlan.getNombre());
			existingTipoPlan.setDescripcion((tipoPlanDTO.getDescripcion() != null) ? tipoPlanDTO.getDescripcion()
					: existingTipoPlan.getNombre());
			existingTipoPlan.setActivo(
					(tipoPlanDTO.getActivo() != null) ? tipoPlanDTO.getActivo() : existingTipoPlan.getActivo());
			TipoPlanDTO tipoPlanDTOR = TipoPlanMapper.INSTANCE
					.entityToDto((TipoPlanEntity) this.tipoPlanRepository.save(existingTipoPlan));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(tipoPlanDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteTipoPlan(Integer id) {
		try {
			log.info("Inicio metodo eliminar tipo de plan por id");
			this.tipoPlanRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Tipo de plan no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
