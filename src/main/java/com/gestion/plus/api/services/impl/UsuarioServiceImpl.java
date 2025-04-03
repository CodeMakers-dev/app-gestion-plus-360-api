package com.gestion.plus.api.services.impl;

import java.util.ArrayList;

import java.util.Date;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.config.JwtTokenUtil;
import com.gestion.plus.api.service.IUsuarioService;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.maps.UsuarioMapper;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.ResponseMessages;
import com.gestion.plus.commons.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

	private final UsuarioRepository usuarioRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtTokenUtil jwtTokenUtil;

	private final EmailServiceImpl emailServiceImpl;

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
			ResponseDTO errorResponse = ResponseDTO.builder().success(false).message(ResponseMessages.SAVE_ERROR)
					.code(HttpStatus.BAD_REQUEST.value()).build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	public ResponseEntity<ResponseDTO> updatePassword(String token, UsuarioDTO usuarioDTO) {
		if (token == null || token.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message(ResponseMessages.TOKEN_REQUIRED).code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		if (jwtTokenUtil.isTokenInvalid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message(ResponseMessages.TOKEN_INVALID).code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		String usuario = jwtTokenUtil.getUsernameFromToken(token);
		if (usuario == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().success(false)
					.message(ResponseMessages.TOKEN_EXPIRED).code(HttpStatus.UNAUTHORIZED.value()).build());
		}

		return usuarioRepository.findByUsuario(usuario).map(usuarioEntity -> {
			String nuevaPassword = usuarioDTO.getPassword();

			if (!validarPassword(nuevaPassword)) {
				
				return ResponseEntity.badRequest().body(ResponseDTO.builder().success(false).message(
						ResponseMessages.SPECIAL_CHARACTERS)
						.code(HttpStatus.BAD_REQUEST.value()).build());
			}

			if (passwordEncoder.matches(nuevaPassword, usuarioEntity.getPassword())) {
				
				return ResponseEntity.badRequest()
						.body(ResponseDTO.builder().success(false)
								.message(ResponseMessages.PASSWORD_DIFFERENT)
								.code(HttpStatus.BAD_REQUEST.value()).build());
			}
			usuarioEntity.setPassword(passwordEncoder.encode(nuevaPassword));
			usuarioEntity.setFechaModificacion(new Date());
			usuarioRepository.save(usuarioEntity);

			jwtTokenUtil.invalidateToken(token);
			return ResponseEntity.ok(ResponseDTO.builder().success(true)
					.message(ResponseMessages.PASSWORD_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).build());
		}).orElseGet(() -> {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().success(false)
					.message(Constantes.USER_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build());
		});
	}

	private boolean validarPassword(String password) {
		return password != null && password.length() >= 8 && password.matches(".*[A-Z].*")
				&& password.matches(".*[a-z].*") && password.matches(".*\\d.*") && password.matches(".*[@#$%^&+=!].*");
	}

	public ResponseEntity<ResponseDTO> findUsuarioById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener los Usuarios por id");
		Optional<UsuarioEntity> usuarioOptional = this.usuarioRepository.findById(id);
		if (usuarioOptional.isPresent()) {
			UsuarioDTO usuarioDTO = UsuarioMapper.INSTANCE.entityToDto(usuarioOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(usuarioDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("usuario no encontrado para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> recoverPassword(String usuario) {
		log.info("Inicio método crear Password para el usuario: {}", usuario);

		if (usuario == null || usuario.isEmpty()) {
			return new ResponseEntity<>(
					Utils.mapearRespuesta(ResponseMessages.USER_REQUIRED, HttpStatus.BAD_REQUEST.value()),
					HttpStatus.BAD_REQUEST);
		}

		Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByUsuario(usuario);

		if (!usuarioOpt.isPresent()) {
			log.warn("Usuario no encontrado: {}", usuario);
			return new ResponseEntity<>(Utils.mapearRespuesta(Constantes.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()),
					HttpStatus.NOT_FOUND);
		}

		UsuarioEntity usuarioEntity = usuarioOpt.get();

		if (!Boolean.TRUE.equals(usuarioEntity.getActivo())) {
			return new ResponseEntity<>(
					Utils.mapearRespuesta(ResponseMessages.USER_INVALID,
							HttpStatus.UNAUTHORIZED.value()),
					HttpStatus.UNAUTHORIZED);
		}
		UserDetails userDetails = new User(usuarioEntity.getUsuario(), usuarioEntity.getPassword(), new ArrayList<>());
		String tokenRecuperacion = jwtTokenUtil.generateToken(userDetails);

		String enlaceRecuperacion = "http://localhost:4200/password?token=" + tokenRecuperacion;

		String mensaje = "<p><strong>¿Olvidaste tu contraseña?</strong></p>"
				+ "<p>Recibimos una solicitud para restablecer tu contraseña.</p>"
				+ "<p>Haz clic en el siguiente enlace para definir una nueva contraseña:</p>" + "<p><a href='"
				+ enlaceRecuperacion + "'>Restablecer contraseña</a></p>";

		emailServiceImpl.sendEmail(usuarioEntity.getUsuario(), Constantes.PASSWORD_RECOVER, mensaje);

		return new ResponseEntity<>(
				Utils.mapearRespuesta(Constantes.EMAIL_USER,
						HttpStatus.OK.value()),
				HttpStatus.OK);
	}

	public ResponseEntity<ResponseDTO> editPassword(UsuarioDTO usuarioDTO) {
		log.info("Inicio método actualizar contraseña para usuario ID: {}", usuarioDTO.getId());

		String nuevaPassword = usuarioDTO.getPassword();

		if (!validarPassword(nuevaPassword)) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().success(false).message(ResponseMessages.SPECIAL_CHARACTERS)
					.code(HttpStatus.BAD_REQUEST.value()).build());
		}

		return usuarioRepository.findById(usuarioDTO.getId()).map(usuario -> {
			if (passwordEncoder.matches(nuevaPassword, usuario.getPassword())) {
				return ResponseEntity.badRequest()
						.body(ResponseDTO.builder().success(false)
								.message(ResponseMessages.PASSWORD_DIFFERENT)
								.code(HttpStatus.BAD_REQUEST.value()).build());
			}

			usuario.setPassword(passwordEncoder.encode(nuevaPassword));
			usuario.setFechaModificacion(new Date());
			usuarioRepository.save(usuario);

			return ResponseEntity.ok(ResponseDTO.builder().success(true).message(ResponseMessages.PASSWORD_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).build());
		}).orElseGet(() -> {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().success(false)
					.message(Constantes.USER_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build());
		});
	}
	
	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metodo Obtener todos los usuarios");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true)).message(ResponseMessages.CONSULTED_SUCCESSFULLY)
					.code(Integer.valueOf(HttpStatus.OK.value()))
					.response(UsuarioMapper.INSTANCE.beanListToDtoList(this.usuarioRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(ResponseMessages.CONSULTING_ERROR)
					.code(Integer.valueOf(HttpStatus.BAD_REQUEST.value())).response(ResponseMessages.NO_RECORD_FOUND)
					.build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}
	
	public ResponseEntity<ResponseDTO> findUsuarioByPersonaId(Integer personaId) {
        log.info("Inicio del metodo para obtener el Usuario por ID de Persona");
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findByPersonaId(personaId);
        if (usuarioOptional.isPresent()) {
            UsuarioDTO usuarioDTO = UsuarioMapper.INSTANCE.entityToDto(usuarioOptional.get());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(Boolean.TRUE)
                    .message(ResponseMessages.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(usuarioDTO)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(Boolean.FALSE)
                    .message("Usuario no encontrado para el ID de Persona: " + personaId)
                    .code(HttpStatus.NOT_FOUND.value())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }
}
