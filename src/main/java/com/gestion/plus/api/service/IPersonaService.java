package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;
import com.gestion.plus.commons.dtos.PersonaDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IPersonaService {

	ResponseEntity<ResponseDTO> savePersona(PersonaDTO personaDTO);

	ResponseEntity<ResponseDTO> findAll();

	ResponseEntity<ResponseDTO> findPersonaById(Integer Id);

	ResponseEntity<ResponseDTO> deletePersona(Integer Id);

	ResponseEntity<ResponseDTO> updatePersona(Integer Id, PersonaDTO personaDTO);
	
	ResponseEntity<ResponseDTO> blockPersona(Integer id);
	
	ResponseEntity<ResponseDTO> updateImagenPersona(Integer id, byte[] imagen);
}
