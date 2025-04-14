package com.gestion.plus.api.services.impl;

import java.util.Date;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IMensajeMasivoService;
import com.gestion.plus.commons.dtos.ArchivoMensajeDTO;
import com.gestion.plus.commons.dtos.ButtonMensajeDTO;
import com.gestion.plus.commons.dtos.MensajeMasivoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.ArchivoMensajeEntity;
import com.gestion.plus.commons.entities.ButtonMensajeEntity;
import com.gestion.plus.commons.entities.MensajeMasivoEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.maps.ArchivoMensajeMapper;
import com.gestion.plus.commons.maps.ButtonMensajeMapper;
import com.gestion.plus.commons.maps.MensajeMasivoMapper;
import com.gestion.plus.commons.repositories.ArchivoMensajeRepository;
import com.gestion.plus.commons.repositories.ButtonMensajeRepository;
import com.gestion.plus.commons.repositories.MensajeMasivoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeMasivoServiceImpl implements IMensajeMasivoService {

	private final MensajeMasivoRepository mensajeMasivoRepository;
    private final ArchivoMensajeRepository archivoMensajeRepository;
    private final ButtonMensajeRepository buttonMensajeRepository;

    @Transactional
    public ResponseEntity<ResponseDTO> sendMensajeMasivo(MensajeMasivoDTO mensajeMasivoDTO,
                                                         List<ArchivoMensajeDTO> archivosDTO,
                                                         List<ButtonMensajeDTO> botonesDTO,
                                                         UsuarioEntity usuario) {
        log.info("Inicio método guardar mensaje masivo");

        try {
           
            mensajeMasivoDTO.setUsuario(usuario);
            mensajeMasivoDTO.setUsuarioCreacion(usuario.getUsuarioCreacion());
            mensajeMasivoDTO.setFechaCreacion(new Date());

       
            if (mensajeMasivoDTO.getTitulo() == null || mensajeMasivoDTO.getMensaje() == null) {
                return ResponseEntity.badRequest().body(ResponseDTO.builder()
                        .success(false)
                        .message("El título y mensaje son obligatorios")
                        .code(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            MensajeMasivoEntity mensajeMasivoEntity = Mappers.getMapper(MensajeMasivoMapper.class)
                    .dtoToEntity(mensajeMasivoDTO);
            mensajeMasivoEntity = mensajeMasivoRepository.save(mensajeMasivoEntity);

            MensajeMasivoDTO savedMensajeMasivoDTO = Mappers.getMapper(MensajeMasivoMapper.class)
                    .entityToDto(mensajeMasivoEntity);

            if (archivosDTO != null && !archivosDTO.isEmpty()) {
                for (ArchivoMensajeDTO archivoDTO : archivosDTO) {
                    archivoDTO.setMensajeMasivo(mensajeMasivoEntity);
                    archivoDTO.setUsuarioCreacion(usuario.getUsuarioCreacion());
                    archivoDTO.setFechaCreacion(new Date());

 
                    if (archivoDTO.getArchivo() == null || archivoDTO.getArchivo().length == 0) {
                        return ResponseEntity.badRequest().body(ResponseDTO.builder()
                                .success(false)
                                .message("El archivo es obligatorio en ArchivoMensajeDTO")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .build());
                    }

                    ArchivoMensajeEntity archivoEntity = Mappers.getMapper(ArchivoMensajeMapper.class)
                            .dtoToEntity(archivoDTO);
                    archivoMensajeRepository.save(archivoEntity);
                }
            }

          
            if (botonesDTO != null && !botonesDTO.isEmpty()) {
                for (ButtonMensajeDTO botonDTO : botonesDTO) {
                    botonDTO.setMensajeMasivo(mensajeMasivoEntity);
                    botonDTO.setUsuarioCreacion(usuario.getUsuarioCreacion());
                    botonDTO.setFechaCreacion(new Date());

                  
                    if (botonDTO.getButton() == null || botonDTO.getButton().isEmpty()) {
                        return ResponseEntity.badRequest().body(ResponseDTO.builder()
                                .success(false)
                                .message("El botón es obligatorio en ButtonMensajeDTO")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .build());
                    }

                    ButtonMensajeEntity botonEntity = Mappers.getMapper(ButtonMensajeMapper.class)
                            .dtoToEntity(botonDTO);
                    buttonMensajeRepository.save(botonEntity);
                }
            }

            log.info("Fin método guardar mensaje masivo");
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message("Mensaje masivo guardado correctamente")
                    .code(HttpStatus.CREATED.value())
                    .response(savedMensajeMasivoDTO)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Exception e) {
            log.error("Error al guardar el mensaje masivo", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message("Error al guardar el mensaje masivo: " + e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}