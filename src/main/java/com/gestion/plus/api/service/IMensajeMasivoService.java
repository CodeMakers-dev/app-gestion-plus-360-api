package com.gestion.plus.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.ArchivoMensajeDTO;
import com.gestion.plus.commons.dtos.ButtonMensajeDTO;
import com.gestion.plus.commons.dtos.MensajeMasivoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.UsuarioEntity;

public interface IMensajeMasivoService {
	
	ResponseEntity<ResponseDTO> sendMensajeMasivo(MensajeMasivoDTO mensajeMasivoDTO,
            List<ArchivoMensajeDTO> archivosDTO,
            List<ButtonMensajeDTO> botonesDTO,
            UsuarioEntity usuario);
	
}
