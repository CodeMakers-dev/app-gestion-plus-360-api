package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.IDestinoPagoService;

import com.gestion.plus.commons.dtos.DestinoPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.DestinoPagoEntity;
import com.gestion.plus.commons.maps.DestinoPagoMapper;
import com.gestion.plus.commons.repositories.DestinoPagoRepository;
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
public class DestinoPagoServiceImpl implements IDestinoPagoService {

	private final DestinoPagoRepository destinoPagoRepository;

	public ResponseEntity<ResponseDTO> saveDestinoPago(DestinoPagoDTO destinoPagoDTO) {
		log.info("Inicio metodo guardar destino de Pago");
		try {
			DestinoPagoEntity destinoPagoEntity = DestinoPagoMapper.INSTANCE.dtoToEntity(destinoPagoDTO);
			destinoPagoEntity.setFechaCreacion(new Date());
			destinoPagoEntity.setActivo(Boolean.valueOf(true));
			DestinoPagoEntity savedEntity = (DestinoPagoEntity) this.destinoPagoRepository.save(destinoPagoEntity);
			DestinoPagoDTO savedDTO = DestinoPagoMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin metodo guardar destino de pago");
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
		log.info("Inicio mObtener todos los destino de pago");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(DestinoPagoMapper.INSTANCE.beanListToDtoList(this.destinoPagoRepository.findAll()))
					.build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findDestinoPagoById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del mpara obtener destino de pago por id");
		Optional<DestinoPagoEntity> destinoPagoOptional = this.destinoPagoRepository.findById(id);
		if (destinoPagoOptional.isPresent()) {
			DestinoPagoDTO destinoPagoDTO = DestinoPagoMapper.INSTANCE.entityToDto(destinoPagoOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(destinoPagoDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("Destino de pago no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateDestinoPago(Integer id, DestinoPagoDTO destinoPagoDTO) {
		ResponseDTO responseDTO;
		DestinoPagoEntity existingDestinoPago = this.destinoPagoRepository.findById(id).orElse(null);
		if (existingDestinoPago != null) {
			existingDestinoPago.setNombre((destinoPagoDTO.getNombre() != null) ? destinoPagoDTO.getNombre()
					: existingDestinoPago.getNombre());
			existingDestinoPago
					.setDescripcion((destinoPagoDTO.getDescripcion() != null) ? destinoPagoDTO.getDescripcion()
							: existingDestinoPago.getNombre());
			existingDestinoPago.setActivo((destinoPagoDTO.getActivo() != null) ? destinoPagoDTO.getActivo()
					: existingDestinoPago.getActivo());
			DestinoPagoDTO destinoPagoDTOR = DestinoPagoMapper.INSTANCE
					.entityToDto((DestinoPagoEntity) this.destinoPagoRepository.save(existingDestinoPago));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(destinoPagoDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteDestinoPago(Integer id) {
		try {
			log.info("Inicio metodo eliminar destino de pago por id");
			this.destinoPagoRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("destino de pago no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}