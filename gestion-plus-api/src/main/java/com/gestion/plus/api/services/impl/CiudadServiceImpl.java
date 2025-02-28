package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.ICiudadService;

import com.gestion.plus.commons.dtos.CiudadDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.CiudadEntity;
import com.gestion.plus.commons.entities.DepartamentoEntity;
import com.gestion.plus.commons.maps.CiudadMapper;
import com.gestion.plus.commons.maps.DepartamentoMapper;
import com.gestion.plus.commons.repositories.CiudadRepository;
import com.gestion.plus.commons.repositories.DepartamentoRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CiudadServiceImpl implements ICiudadService {

	private final CiudadRepository ciudadRepository;

	private final DepartamentoRepository departamentoRepository;

	public ResponseEntity<ResponseDTO> saveCiudad(CiudadDTO ciudadDTO) {
		log.info("Inicio metodo guardar ciudad");
		try {
			ResponseDTO responseDTO;
			Optional<DepartamentoEntity> departamentoOptional = this.departamentoRepository
					.findById(ciudadDTO.getDepartamento().getId());
			if (departamentoOptional.isPresent()) {
				CiudadEntity savedCiudad = (CiudadEntity) this.ciudadRepository
						.save(CiudadMapper.INSTANCE.dtoToEntity(ciudadDTO));
				CiudadDTO lastInsertCiudad = CiudadMapper.INSTANCE.entityToDto(savedCiudad);
				lastInsertCiudad.setDepartamento(DepartamentoMapper.INSTANCE.entityToDto(departamentoOptional.get()));
				log.info("Fin metodo guardar ciudad");
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
						.message(ResponseMessages.SAVED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.CREATED.value()))
						.response(lastInsertCiudad).build();
			} else {
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(Constantes.DEPARTMENT_ERROR)
						.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(null).build();
			}
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.SAVE_ERROR).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response(null).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}

	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo obtener todos las ciudades");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(CiudadMapper.INSTANCE.beanListToDtoList(this.ciudadRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.CONSULTING_ERROR).code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findCiudadById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener ciudad por id");
		Optional<CiudadEntity> ciudadOptional = this.ciudadRepository.findById(id);
		if (ciudadOptional.isPresent()) {
			CiudadDTO ciudadDTO = CiudadMapper.INSTANCE.entityToDto(ciudadOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(ciudadDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("Ciudad no encontrada para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateCiudad(Integer id, CiudadDTO ciudadDTO) {
		ResponseDTO responseDTO;
		CiudadEntity existingCiudad = this.ciudadRepository.findById(id).orElse(null);
		if (existingCiudad != null) {
			existingCiudad
					.setNombre((ciudadDTO.getNombre() != null) ? ciudadDTO.getNombre() : existingCiudad.getNombre());
			existingCiudad
					.setActivo((ciudadDTO.getActivo() != null) ? ciudadDTO.getActivo() : existingCiudad.getActivo());
			CiudadDTO ciudadDTOR = CiudadMapper.INSTANCE
					.entityToDto((CiudadEntity) this.ciudadRepository.save(existingCiudad));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(ciudadDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteCiudad(Integer id) {
		try {
			log.info("Inicio metodo eliminar ciudad por id");
			this.ciudadRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Ciudad no encontrada para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
