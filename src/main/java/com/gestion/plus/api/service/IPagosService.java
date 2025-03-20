package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;


import com.gestion.plus.commons.dtos.PagosDTO;
import com.gestion.plus.commons.dtos.PersonaDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface IPagosService {

	ResponseEntity<ResponseDTO> savePagos(PagosDTO pagosDTO);

	ResponseEntity<ResponseDTO> getPagoByIdPersona(PersonaDTO idPersona);

	ResponseEntity<ResponseDTO> findAll();

	ResponseEntity<ResponseDTO> deletePago(Integer Id);

	ResponseEntity<ResponseDTO> updatePago(Integer Id, PagosDTO pagosDTO);
	
	ResponseEntity<ResponseDTO> findPagosById(Integer Id);
}
