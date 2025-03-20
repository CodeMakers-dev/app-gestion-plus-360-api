package com.gestion.plus.api.services.impl;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import com.gestion.plus.api.service.IMensajeService;
import com.gestion.plus.commons.dtos.MensajeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.MensajeEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.maps.MensajeMapper;
import com.gestion.plus.commons.repositories.MensajeRepository;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.utils.Constantes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeServiceImpl implements IMensajeService{
	
	@PersistenceContext
    private EntityManager entityManager;
	
	private final MensajeRepository mensajeRepository;
	
    private final UsuarioRepository usuarioRepository;
    
    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> sendMensaje(MensajeDTO mensajeDTO) {
        log.info("Inicio método enviar mensaje");
        try {
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(mensajeDTO.getUsuario().getId());

            if (usuarioOpt.isPresent()) {
                MensajeEntity mensajeEntity;
                if (mensajeDTO.getId() != null) {
                    mensajeEntity = entityManager.find(MensajeEntity.class, mensajeDTO.getId());
                    if (mensajeEntity != null) {
                        mensajeEntity.setActivo(mensajeDTO.getActivo());
                        mensajeEntity.setTitulo(mensajeDTO.getTitulo());
                        mensajeEntity.setDescripcion(mensajeDTO.getDescripcion());
                        mensajeEntity.setUsuario(usuarioOpt.get());
                        mensajeEntity.setFechaEnvio(new Date());
                        mensajeEntity = entityManager.merge(mensajeEntity);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ResponseDTO.builder()
                                        .success(false)
                                        .message("Mensaje no encontrado")
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .build());
                    }
                } else {
                    mensajeEntity = MensajeMapper.INSTANCE.dtoToEntity(mensajeDTO);
                    mensajeEntity.setUsuario(usuarioOpt.get());
                    mensajeEntity.setFechaEnvio(new Date());
                    mensajeEntity.setFechaCreacion(new Date());
                    mensajeEntity = mensajeRepository.save(mensajeEntity);
                }

                MensajeDTO mensajeResponseDTO = MensajeMapper.INSTANCE.entityToDto(mensajeEntity);

                log.info("Mensaje enviado con éxito");
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(true)
                        .message(Constantes.SUCCESSFUL_MESSAGE)
                        .code(HttpStatus.CREATED.value())
                        .response(mensajeResponseDTO)
                        .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDTO.builder()
                                .success(false)
                                .message(Constantes.USER_NOT_FOUND)
                                .code(HttpStatus.NOT_FOUND.value())
                                .build());
            }
        } catch (Exception e) {
            log.error("Error al enviar el mensaje: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.ERROR_MESSAGE)
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }
    
    @Override
    public ResponseEntity<ResponseDTO>sendMensajeAll(MensajeDTO mensajeDTO){
    	log.info("Inicio método enviar mensaje a todos los usuarios");
        try {
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();

            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDTO.builder()
                                .success(false)
                                .message(Constantes.USER_ERROR)
                                .code(HttpStatus.NOT_FOUND.value())
                                .build());
            }

            List<MensajeDTO> mensajesEnviados = new ArrayList<>();

            for (UsuarioEntity usuario : usuarios) {
                MensajeEntity mensajeEntity = MensajeMapper.INSTANCE.dtoToEntity(mensajeDTO);
                mensajeEntity.setUsuario(usuario);
                mensajeEntity.setFechaEnvio(new Date());
                mensajeEntity.setFechaCreacion(new Date());
                mensajeEntity.setActivo(true);

                MensajeEntity mensajeGuardado = mensajeRepository.save(mensajeEntity);
                mensajesEnviados.add(MensajeMapper.INSTANCE.entityToDto(mensajeGuardado));
            }

            log.info("Mensajes enviados con éxito a todos los usuarios");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.builder()
                            .success(true)
                            .message(Constantes.MESSAGE_SUCCESSFUL_USERS)
                            .code(HttpStatus.CREATED.value())
                            .response(mensajesEnviados)
                            .build());
        } catch (Exception e) {
            log.error("Error al enviar los mensajes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.ERROR_MESSAGES)
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> findMensajeById(Integer idUsuario) {
        log.info("Inicio método obtener mensajes por ID de usuario");

        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isPresent()) {
            List<MensajeEntity> mensajes = mensajeRepository.findByUsuario(usuarioOpt.get());
            List<MensajeDTO> mensajesDTO = MensajeMapper.INSTANCE.beanListToDtoList(mensajes);

            return ResponseEntity.ok(ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.MESSAGE_FOUND)
                    .code(HttpStatus.OK.value())
                    .response(mensajesDTO)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.USER_NOT_FOUND)
                            .code(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Inicio método obtener todos los mensajes");

        try {
            List<MensajeEntity> mensajes = mensajeRepository.findAll();
            List<MensajeDTO> mensajesDTO = MensajeMapper.INSTANCE.beanListToDtoList(mensajes);

            return ResponseEntity.ok(ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.LIST_MESSAGE)
                    .code(HttpStatus.OK.value())
                    .response(mensajesDTO)
                    .build());
        } catch (Exception e) {
            log.error("Error al obtener mensajes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.ERROR_GET_MESSAGE)
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }
}