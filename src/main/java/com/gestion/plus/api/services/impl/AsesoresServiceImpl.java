package com.gestion.plus.api.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IAsesoresService;
import com.gestion.plus.commons.dtos.AsesoresDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.AsesoresEntity;
import com.gestion.plus.commons.maps.AsesoresMapper;
import com.gestion.plus.commons.repositories.AsesoresRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsesoresServiceImpl implements IAsesoresService  {

	private final AsesoresRepository asesoresRepository;

	public ResponseEntity<ResponseDTO> saveAsesor(AsesoresDTO asesoresDTO) {
		log.info("Inicio metodo guardar asesor");
		try {
			AsesoresEntity asesoresEntity = AsesoresMapper.INSTANCE.dtoToEntity(asesoresDTO);
			asesoresEntity.setFechaCreacion(new Date());
			asesoresEntity.setActivo(Boolean.valueOf(true));
			AsesoresEntity savedEntity = (AsesoresEntity) this.asesoresRepository.save(asesoresEntity);
			AsesoresDTO savedDTO = AsesoresMapper.INSTANCE.entityToDto(savedEntity);
			log.info("Fin metodo guardar asesor");
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
		log.info("Inicio metodo Obtener todos los asesores");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(AsesoresMapper.INSTANCE.beanListToDtoList(this.asesoresRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findAsesorById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los asesores por id");
		Optional<AsesoresEntity> AsesoresOptional = this.asesoresRepository.findById(id);
		if (AsesoresOptional.isPresent()) {
			AsesoresDTO asesoresDTO = AsesoresMapper.INSTANCE.entityToDto(AsesoresOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(asesoresDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("asesor no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteAsesor(Integer id) {
		try {
			log.info("Inicio metodo eliminar asesor por id");
			this.asesoresRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("asesor no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
