package com.gestion.plus.api.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.IUsuarioService;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.maps.UsuarioMapper;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

	private final UsuarioRepository usuarioRepository;

	private final PasswordEncoder passwordEncoder;

	public ResponseEntity<ResponseDTO> saveUsuario(UsuarioDTO usuarioDTO) {
		log.info("Inicio metodo guardar Usuario");
		try {
			UsuarioEntity usuarioEntity = UsuarioMapper.INSTANCE.dtoToEntity(usuarioDTO);
			usuarioEntity.setFechaCreacion(new Date());
			usuarioEntity.setActivo(true);

			String hashedPassword = passwordEncoder.encode(usuarioDTO.getPassword());
			usuarioEntity.setPassword(hashedPassword);

			UsuarioEntity savedEntity = usuarioRepository.save(usuarioEntity);
			UsuarioDTO savedDTO = UsuarioMapper.INSTANCE.entityToDto(savedEntity);

			log.info("Fin metodo guardar Usuario");
			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(ResponseMessages.SAVED_SUCCESSFULLY)
					.code(HttpStatus.CREATED.value()).response(savedDTO).build();

			return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
		} catch (Exception e) {
			log.error("Error al guardar usuario", e);
			ResponseDTO errorResponse = ResponseDTO.builder().success(false).message(ResponseMessages.SAVE_ERROR)
					.code(HttpStatus.BAD_REQUEST.value()).build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}
	
	public ResponseEntity<ResponseDTO> updatePassword(UsuarioDTO usuarioDTO) {
	    log.info("Inicio método actualizar contraseña para usuario ID: {}", usuarioDTO.getId());

	    if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().trim().isEmpty()) {
	        log.warn("Contraseña inválida para usuario ID: {}", usuarioDTO.getId());
	        return ResponseEntity.badRequest().body(
	            ResponseDTO.builder()
	                .success(false)
	                .message("La contraseña no puede estar vacía")
	                .code(HttpStatus.BAD_REQUEST.value())
	                .build()
	        );
	    }

	    return usuarioRepository.findById(usuarioDTO.getId())
	        .map(usuario -> {
	            String hashedPassword = passwordEncoder.encode(usuarioDTO.getPassword());

	            if (passwordEncoder.matches(usuarioDTO.getPassword(), usuario.getPassword())) {
	                log.warn("La nueva contraseña no puede ser igual a la actual para usuario ID: {}", usuarioDTO.getId());
	                return ResponseEntity.badRequest().body(
	                    ResponseDTO.builder()
	                        .success(false)
	                        .message("La nueva contraseña debe ser diferente a la actual")
	                        .code(HttpStatus.BAD_REQUEST.value())
	                        .build()
	                );
	            }

	            usuario.setPassword(hashedPassword);
	            usuario.setFechaModificacion(new Date());
	            usuarioRepository.save(usuario);

	            log.info("Contraseña actualizada correctamente para usuario ID: {}", usuarioDTO.getId());
	            return ResponseEntity.ok(
	                ResponseDTO.builder()
	                    .success(true)
	                    .message("Contraseña actualizada correctamente")
	                    .code(HttpStatus.OK.value())
	                    .build()
	            );
	        })
	        .orElseGet(() -> {
	            log.error("Usuario no encontrado con ID: {}", usuarioDTO.getId());
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                ResponseDTO.builder()
	                    .success(false)
	                    .message("Usuario no encontrado")
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build()
	            );
	        });
	}
	
	public ResponseEntity<ResponseDTO> findUsuarioById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los Usuarios por id");
		Optional<UsuarioEntity> usuarioOptional = this.usuarioRepository.findById(id);
		if (usuarioOptional.isPresent()) {
			UsuarioDTO usuarioDTO = UsuarioMapper.INSTANCE.entityToDto(usuarioOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value())).response(usuarioDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("usuario no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

}
