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
    log.info("Inicio mguardar ciudad");
    try {
      ResponseDTO responseDTO;
      Optional<DepartamentoEntity> departamentoOptional = this.departamentoRepository.findById(ciudadDTO.getDepartamento().getId());
      if (departamentoOptional.isPresent()) {
        CiudadEntity savedCiudad = (CiudadEntity)this.ciudadRepository.save(CiudadMapper.INSTANCE.dtoToEntity(ciudadDTO));
        CiudadDTO lastInsertCiudad = CiudadMapper.INSTANCE.entityToDto(savedCiudad);
        lastInsertCiudad.setDepartamento(DepartamentoMapper.INSTANCE.entityToDto(departamentoOptional.get()));
        log.info("Fin mguardar ciudad");
        responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message("Guardado exitosamente").code(Integer.valueOf(HttpStatus.CREATED.value())).response(lastInsertCiudad).build();
      } else {
        responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message("Departamento no encontrado").code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(null).build();
      } 
      return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
    } catch (Exception e) {
      ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message("Error al guardar").code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(null).build();
      return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
    } 
  }
  
  public ResponseEntity<ResponseDTO> findAll() {
    ResponseDTO responseDTO;
    log.info("Inicio mobtener todos las ciudades");
    try {
      responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message("Consulta exitosamente").code(Integer.valueOf(HttpStatus.OK.value())).response(CiudadMapper.INSTANCE.beanListToDtoList(this.ciudadRepository.findAll())).build();
    } catch (Exception e) {
      responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message("Error al consultar").code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response("No se ha encontrado ningregistro").build();
    } 
    return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
  }
  
  public ResponseEntity<ResponseDTO> findCiudadById(Integer id) {
    ResponseDTO responseDTO;
    log.info("Inicio del mpara obtener ciudad por id");
    Optional<CiudadEntity> ciudadOptional = this.ciudadRepository.findById(id);
    if (ciudadOptional.isPresent()) {
      CiudadDTO ciudadDTO = CiudadMapper.INSTANCE.entityToDto(ciudadOptional.get());
      responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message("Consulta exitosamente").code(Integer.valueOf(HttpStatus.OK.value())).response(ciudadDTO).build();
    } else {
      responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).message("Ciudad no encontrada para el Id: " + id).response(null).build();
    } 
    return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
  }
  
  public ResponseEntity<ResponseDTO> updateCiudad(Integer id, CiudadDTO ciudadDTO) {
    ResponseDTO responseDTO;
    CiudadEntity existingCiudad = this.ciudadRepository.findById(id).orElse(null);
    if (existingCiudad != null) {
      existingCiudad.setNombre(
          (ciudadDTO.getNombre() != null) ? ciudadDTO.getNombre() : existingCiudad.getNombre());
      existingCiudad.setActivo(
          (ciudadDTO.getActivo() != null) ? ciudadDTO.getActivo() : existingCiudad.getActivo());
      CiudadDTO ciudadDTOR = CiudadMapper.INSTANCE.entityToDto((CiudadEntity)this.ciudadRepository.save(existingCiudad));
      responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message("Actualizado exitosamente").code(Integer.valueOf(HttpStatus.OK.value())).response(ciudadDTOR).build();
    } else {
      responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message("Error al actualizar").code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(HttpStatus.NOT_FOUND).build();
    } 
    return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
  }
  
  public ResponseEntity<ResponseDTO> deleteCiudad(Integer id) {
    try {
      log.info("Inicio metodo eliminar ciudad por id");
      this.ciudadRepository.deleteById(id);
      ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message("Eliminado exitosamente").code(Integer.valueOf(HttpStatus.OK.value())).response(HttpStatus.OK).build();
      return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
    } catch (Exception e) {
      ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message("Error al eliminar, registro no encontrado").code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response("Ciudad no encontrada para el Id: " + id).build();
      return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
    } 
  }
}
