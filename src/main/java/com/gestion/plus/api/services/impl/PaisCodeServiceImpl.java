package com.gestion.plus.api.services.impl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IPaisCodeService;
import com.gestion.plus.commons.dtos.PaisCodeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.PaisCodeEntity;
import com.gestion.plus.commons.entities.PaisEntity;
import com.gestion.plus.commons.maps.PaisCodeMapper;
import com.gestion.plus.commons.maps.PaisMapper;
import com.gestion.plus.commons.repositories.PaisCodeRepository;
import com.gestion.plus.commons.repositories.PaisRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaisCodeServiceImpl implements IPaisCodeService {

	private final PaisCodeRepository paisCodeRepository;

	private final PaisRepository paisRepository;

	public ResponseEntity<ResponseDTO> savePaisCode(PaisCodeDTO paisCodeDTO) {
		log.info("Inicio metodo guardar pais + codigo");
		try {
			ResponseDTO responseDTO;
			Optional<PaisEntity> paisOptional = this.paisRepository.findById(paisCodeDTO.getPais().getId());
			if (paisOptional.isPresent()) {
				PaisCodeEntity savedPaisCode = (PaisCodeEntity) this.paisCodeRepository
						.save(PaisCodeMapper.INSTANCE.dtoToEntity(paisCodeDTO));
				PaisCodeDTO lastInsertPaisCode = PaisCodeMapper.INSTANCE.entityToDto(savedPaisCode);
				lastInsertPaisCode.setPais(PaisMapper.INSTANCE.entityToDto(paisOptional.get()));
				log.info("Fin metodo guardar pais + codigo");
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.SAVED_SUCCESSFULLY)
						.code(Integer.valueOf(HttpStatus.CREATED.value())).response(lastInsertPaisCode).build();
			} else {
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
						.message(ResponseMessages.COUNTRY_CODE_NOT_FOUND).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
						.response(null).build();
			}
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.SAVE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(null).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}

	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo obtener todos los paises + codigo");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(PaisCodeMapper.INSTANCE.beanListToDtoList(this.paisCodeRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findPaisCodeById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener pais + codigo por id");
		Optional<PaisCodeEntity> paisCodeOptional = this.paisCodeRepository.findById(id);
		if (paisCodeOptional.isPresent()) {
			PaisCodeDTO paisCodeDTO = PaisCodeMapper.INSTANCE.entityToDto(paisCodeOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(paisCodeDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("Pais + codigo no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updatePaisCode(Integer id, PaisCodeDTO paisCodeDTO) {
		ResponseDTO responseDTO;
		PaisCodeEntity existingPaisCode = this.paisCodeRepository.findById(id).orElse(null);
		if (existingPaisCode != null) {
			existingPaisCode.setActivo(
					(paisCodeDTO.getActivo() != null) ? paisCodeDTO.getActivo() : existingPaisCode.getActivo());
			PaisCodeDTO paisCodeDTOR = PaisCodeMapper.INSTANCE
					.entityToDto((PaisCodeEntity) this.paisCodeRepository.save(existingPaisCode));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(paisCodeDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deletePaisCode(Integer id) {
		try {
			log.info("Inicio metodo eliminar Pais + codigo por id");
			this.paisCodeRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Pais + codigo no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
