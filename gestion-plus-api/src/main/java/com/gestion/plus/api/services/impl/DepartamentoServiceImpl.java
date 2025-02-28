package com.gestion.plus.api.services.impl;

import com.gestion.plus.api.service.IDepartamentoService;

import com.gestion.plus.commons.dtos.DepartamentoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.DepartamentoEntity;
import com.gestion.plus.commons.entities.PaisEntity;
import com.gestion.plus.commons.maps.DepartamentoMapper;
import com.gestion.plus.commons.maps.PaisMapper;
import com.gestion.plus.commons.repositories.DepartamentoRepository;
import com.gestion.plus.commons.repositories.PaisRepository;
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
public class DepartamentoServiceImpl implements IDepartamentoService {

	private final DepartamentoRepository departamentoRepository;

	private final PaisRepository paisRepository;

	public ResponseEntity<ResponseDTO> saveDepartamento(DepartamentoDTO departamentoDTO) {
		log.info("Inicio metodo guardar departamento");
		try {
			ResponseDTO responseDTO;
			Optional<PaisEntity> paisOptional = this.paisRepository.findById(departamentoDTO.getPais().getId());
			if (paisOptional.isPresent()) {
				DepartamentoEntity savedDepartamento = (DepartamentoEntity) this.departamentoRepository
						.save(DepartamentoMapper.INSTANCE.dtoToEntity(departamentoDTO));
				DepartamentoDTO lastInsertDepartamento = DepartamentoMapper.INSTANCE.entityToDto(savedDepartamento);
				lastInsertDepartamento.setPais(PaisMapper.INSTANCE.entityToDto(paisOptional.get()));
				log.info("Fin metodo guardar departamento");
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.SAVED_SUCCESSFULLY)
						.code(Integer.valueOf(HttpStatus.CREATED.value())).response(lastInsertDepartamento).build();
			} else {
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
						.message(Constantes.DEPARTMENT_ERROR).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
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
		log.info("Inicio metodo obtener todos los departamentos");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(DepartamentoMapper.INSTANCE.beanListToDtoList(this.departamentoRepository.findAll()))
					.build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findDepartamentoById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener departamento por id");
		Optional<DepartamentoEntity> departamentoOptional = this.departamentoRepository.findById(id);
		if (departamentoOptional.isPresent()) {
			DepartamentoDTO departamentoDTO = DepartamentoMapper.INSTANCE.entityToDto(departamentoOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(departamentoDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("Departamento no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updateDepartamento(Integer id, DepartamentoDTO departamentoDTO) {
		ResponseDTO responseDTO;
		DepartamentoEntity existingDepartamento = this.departamentoRepository.findById(id).orElse(null);
		if (existingDepartamento != null) {
			existingDepartamento.setNombre((departamentoDTO.getNombre() != null) ? departamentoDTO.getNombre()
					: existingDepartamento.getNombre());
			existingDepartamento.setActivo((departamentoDTO.getActivo() != null) ? departamentoDTO.getActivo()
					: existingDepartamento.getActivo());
			DepartamentoDTO departamentoDTOR = DepartamentoMapper.INSTANCE
					.entityToDto((DepartamentoEntity) this.departamentoRepository.save(existingDepartamento));
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.UPDATED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(departamentoDTOR).build();
		} else {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.UPDATE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> deleteDepartamento(Integer id) {
		try {
			log.info("Inicio metodo eliminar departamento por id");
			this.departamentoRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.DELETED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(HttpStatus.OK).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.DELETE_ERROR)
					.code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response("Departamento no encontrado para el Id: " + id).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
}
