package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.IOrigenPagoService;

import com.gestion.plus.commons.dtos.OrigenPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.OrigenPagoEntity;
import com.gestion.plus.commons.maps.OrigenPagoMapper;
import com.gestion.plus.commons.repositories.OrigenPagoRepository;
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
public class OrigenPagoServiceImpl implements IOrigenPagoService {

	private final OrigenPagoRepository origenPagoRepository;

	public ResponseEntity<ResponseDTO> saveOrigenPago(OrigenPagoDTO origenPagoDTO) {
		log.info("Inicio mguardar Origen de Pago");
		try {
			OrigenPagoEntity origenPagoEntity = OrigenPagoMapper.INSTANCE.dtoToEntity(origenPagoDTO);
			origenPagoEntity.setFechaCreacion(new Date());
			origenPagoEntity.setActivo(Boolean.valueOf(true));
			OrigenPagoEntity savedEntity = (OrigenPagoEntity) this.origenPagoRepository.save(origenPagoEntity);
			OrigenPagoDTO savedDTO = OrigenPagoMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin mguardar origen de pago");
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
		log.info("Inicio mObtener todos los origenes de pago");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(OrigenPagoMapper.INSTANCE.beanListToDtoList(this.origenPagoRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findOrigenPagoById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener origen de pago por id");
		Optional<OrigenPagoEntity> origenPagoOptional = this.origenPagoRepository.findById(id);
		if (origenPagoOptional.isPresent()) {
			OrigenPagoDTO origenPagoDTO = OrigenPagoMapper.INSTANCE.entityToDto(origenPagoOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(origenPagoDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("Origen de pago no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateOrigenPago(Integer id, OrigenPagoDTO origenPagoDTO) {
		ResponseDTO responseDTO;
		OrigenPagoEntity existingOrigenPago = this.origenPagoRepository.findById(id).orElse(null);
		if (existingOrigenPago != null) {
			existingOrigenPago.setNombre(
					(origenPagoDTO.getNombre() != null) ? origenPagoDTO.getNombre() : existingOrigenPago.getNombre());
			existingOrigenPago.setDescripcion((origenPagoDTO.getDescripcion() != null) ? origenPagoDTO.getDescripcion()
					: existingOrigenPago.getNombre());
			existingOrigenPago.setActivo(
					(origenPagoDTO.getActivo() != null) ? origenPagoDTO.getActivo() : existingOrigenPago.getActivo());
			OrigenPagoDTO origenPagoDTOR = OrigenPagoMapper.INSTANCE
					.entityToDto((OrigenPagoEntity) this.origenPagoRepository.save(existingOrigenPago));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(origenPagoDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteOrigenPago(Integer id) {
		try {
			log.info("Inicio metodo eliminar origen de pago por id");
			this.origenPagoRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("orgien de pago no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
