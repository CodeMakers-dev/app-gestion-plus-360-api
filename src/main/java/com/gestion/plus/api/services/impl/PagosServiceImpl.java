package com.gestion.plus.api.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.gestion.plus.api.service.IPagosService;
import com.gestion.plus.commons.dtos.PagosDTO;
import com.gestion.plus.commons.dtos.PersonaDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.PagosEntity;
import com.gestion.plus.commons.entities.PersonaEntity;
import com.gestion.plus.commons.maps.PagosMapper;
import com.gestion.plus.commons.maps.PersonaMapper;
import com.gestion.plus.commons.repositories.PagosRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagosServiceImpl implements IPagosService {

	private final PagosRepository pagosRepository;

	public ResponseEntity<ResponseDTO> savePagos(PagosDTO pagosDTO) {
		log.info("Inicio metodo guardar pago");

		try {
			PagosEntity pagosEntity = PagosMapper.INSTANCE.dtoToEntity(pagosDTO);
			pagosEntity.setFechaCreacion(new Date());
			pagosEntity.setActivo(true);

			PagosEntity savedEntity = pagosRepository.save(pagosEntity);
			PagosDTO savedDTO = PagosMapper.INSTANCE.entityToDto(savedEntity);

			log.info("Fin metodo guardar pago");

			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(ResponseMessages.SAVED_SUCCESSFULLY)
					.code(201).response(savedDTO).build();
			return ResponseEntity.status(201).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO errorResponse = ResponseDTO.builder().success(false).message(ResponseMessages.SAVE_ERROR)
					.code(400).build();
			return ResponseEntity.status(400).body(errorResponse);
		}
	}

	public ResponseEntity<ResponseDTO> getPagoByIdPersona(PersonaDTO personaDTO) {
		log.info("Inicio método consultar pago por idPersona: {}", personaDTO);

		try {

			PersonaEntity personaEntity = PersonaMapper.INSTANCE.dtoToEntity(personaDTO);

			Optional<PagosEntity> pagosEntityOptional = pagosRepository.findByPersona(personaEntity);

			if (pagosEntityOptional.isPresent()) {
				PagosDTO pagosDTO = PagosMapper.INSTANCE.entityToDto(pagosEntityOptional.get());
				ResponseDTO responseDTO = ResponseDTO.builder().success(true)
						.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(200).response(pagosDTO).build();
				return ResponseEntity.ok(responseDTO);
			} else {
				return ResponseEntity.status(404).body(ResponseDTO.builder().success(false)
						.message(ResponseMessages.NO_RECORD_FOUND).code(404).build());
			}
		} catch (Exception e) {
			log.error("Error al consultar pago por idPersona", e);
			return ResponseEntity.status(500).body(
					ResponseDTO.builder().success(false).message(ResponseMessages.CONSULTING_ERROR).code(500).build());
		}
	}

	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo Obtener todos los pagos");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(PagosMapper.INSTANCE.beanListToDtoList(this.pagosRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.CONSULTING_ERROR).code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deletePago(Integer id) {
		try {
			log.info("Inicio metodo eliminar pago por id");
			this.pagosRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("pago no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}

	public ResponseEntity<ResponseDTO> updatePago(Integer id, PagosDTO pagosDTO) {
	    log.info("Inicio método actualizar pago con id: {}", id);

	    ResponseDTO responseDTO;
	    PagosEntity existingPago = pagosRepository.findById(id).orElse(null);

	    if (existingPago != null) {
	        existingPago.setPersona((pagosDTO.getPersona() != null) ? pagosDTO.getPersona() : existingPago.getPersona());
	        existingPago.setTipoPlan((pagosDTO.getTipoPlan() != null) ? pagosDTO.getTipoPlan() : existingPago.getTipoPlan());
	        existingPago.setOrigenPago((pagosDTO.getOrigenPago() != null) ? pagosDTO.getOrigenPago() : existingPago.getOrigenPago());
	        existingPago.setDestinoPago((pagosDTO.getDestinoPago() != null) ? pagosDTO.getDestinoPago() : existingPago.getDestinoPago());
	        existingPago.setValorPago((pagosDTO.getValorPago() != null) ? pagosDTO.getValorPago() : existingPago.getValorPago());
	        existingPago.setReferencia((pagosDTO.getReferencia() != null) ? pagosDTO.getReferencia() : existingPago.getReferencia());
	        existingPago.setFechaPago((pagosDTO.getFechaPago() != null) ? pagosDTO.getFechaPago() : existingPago.getFechaPago());
	        existingPago.setVigenciaDesde((pagosDTO.getVigenciaDesde() != null) ? pagosDTO.getVigenciaDesde() : existingPago.getVigenciaDesde());
	        existingPago.setVigenciaHasta((pagosDTO.getVigenciaHasta() != null) ? pagosDTO.getVigenciaHasta() : existingPago.getVigenciaHasta());
	        existingPago.setDiasVigencia((pagosDTO.getDiasVigencia() != null) ? pagosDTO.getDiasVigencia() : existingPago.getDiasVigencia());
	        existingPago.setUsuarioModificacion((pagosDTO.getUsuarioModificacion() != null) ? pagosDTO.getUsuarioModificacion() : existingPago.getUsuarioModificacion());

	        existingPago.setFechaModificacion(new Date());

	        PagosDTO updatedDTO = PagosMapper.INSTANCE.entityToDto(pagosRepository.save(existingPago));

	        responseDTO = ResponseDTO.builder()
	                .success(true)
	                .message(ResponseMessages.UPDATED_SUCCESSFULLY)
	                .code(HttpStatus.OK.value())
	                .response(updatedDTO)
	                .build();
	    } else {
	        responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(ResponseMessages.UPDATE_ERROR)
	                .code(HttpStatus.NOT_FOUND.value())
	                .response(HttpStatus.NOT_FOUND)
	                .build();
	    }

	    return ResponseEntity.status(responseDTO.getCode()).body(responseDTO);
	}
}