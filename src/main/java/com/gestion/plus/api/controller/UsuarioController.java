package com.gestion.plus.api.controller;

import java.util.Optional;

import java.util.Map;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.plus.api.config.JwtTokenUtil;
import com.gestion.plus.api.services.impl.AuthenticationServiceImpl;
import com.gestion.plus.api.services.impl.UsuarioServiceImpl;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.entities.VigenciaUsuarioEntity;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.repositories.VigenciaUsuarioRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({ "/api/v1/Usuario" })
@CrossOrigin(origins = { "*" }, methods = { RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT })
@RequiredArgsConstructor
public class UsuarioController {

	private final AuthenticationServiceImpl authenticationServiceImpl;

	private final UsuarioServiceImpl usuarioServiceImpl;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenUtil jwtTokenUtil;

	private final UsuarioRepository usuarioRepository;
	private final VigenciaUsuarioRepository vigenciaUsuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Operation(summary = "Operación que permite validar el usuario")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se ha creado satisfactoriamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })

	@PostMapping("/validar")
	public ResponseEntity<ResponseDTO> validarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		ResponseEntity<ResponseDTO> response = this.authenticationServiceImpl.autenticar(usuarioDTO);

		if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null
				&& response.getBody().getResponse() != null) {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(usuarioDTO.getUsuario(), usuarioDTO.getPassword()));

			final UserDetails userDetails = this.authenticationServiceImpl.loadUserByUsername(usuarioDTO.getUsuario());
			final String token = jwtTokenUtil.generateToken(userDetails);

			Object responseObject = response.getBody().getResponse();
			if (responseObject instanceof UsuarioDTO usuarioTmp) {
				usuarioTmp.setToken(Constantes.BEARER + token);
				response.getBody().setResponse(usuarioTmp);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Utils
						.mapearRespuesta("Error interno: usuario no válido", HttpStatus.INTERNAL_SERVER_ERROR.value()));
			}
		}

		return response;
	}

	@Operation(summary = "Operación que permite crear la contraseña del usuario", 
	           description = "Permite a un usuario cambiar su contraseña utilizando un token válido.")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "Token inválido o expirado", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
	@PostMapping("/reset-password")
	public ResponseEntity<ResponseDTO> resetPassword(
	        @Parameter(description = "Token de autenticación para resetear la contraseña") 
	        @RequestParam String llave,
	        @RequestBody Map<String, String> requestBody) {

	    String newPassword = requestBody.get("password");

	    Optional<VigenciaUsuarioEntity> optionalVigencia = vigenciaUsuarioRepository.findByLlave(llave);

	    if (optionalVigencia.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ResponseDTO(false, "Token inválido", HttpStatus.BAD_REQUEST.value(), null));
	    }

	    VigenciaUsuarioEntity vigencia = optionalVigencia.get();

	    if (!vigencia.isTokenVigente()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ResponseDTO(false, "Token expirado", HttpStatus.BAD_REQUEST.value(), null));
	    }

	    Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(vigencia.getUsuario().getId());

	    if (usuarioExistente.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ResponseDTO(false, "Usuario no encontrado", HttpStatus.BAD_REQUEST.value(), null));
	    }

	    UsuarioEntity usuario = usuarioExistente.get();

	    if (!Boolean.TRUE.equals(usuario.getActivo())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ResponseDTO(false, "Usuario inactivo", HttpStatus.BAD_REQUEST.value(), null));
	    }

	    if (!validarPassword(newPassword)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(false,
	                "Contraseña no cumple con los requisitos", HttpStatus.BAD_REQUEST.value(), null));
	    }

	    usuario.setPassword(passwordEncoder.encode(newPassword));
	    usuarioRepository.save(usuario);

	    vigencia.setActivo(false);
	    vigenciaUsuarioRepository.save(vigencia);

	    return ResponseEntity
	            .ok(new ResponseDTO(true, "Contraseña actualizada correctamente", HttpStatus.OK.value(), null));
	}

	private boolean validarPassword(String password) {
	    return password != null && password.length() >= 8 
	            && password.matches(".*[A-Z].*")
	            && password.matches(".*[a-z].*") 
	            && password.matches(".*\\d.*") 
	            && password.matches(".*[@#$%^&+=!].*");
	}

	@Operation(summary = "Operación que permite actualizar la contraseña de usuario")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se ha creado satisfactoriamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })

	@PutMapping("/password")
	public ResponseEntity<ResponseDTO> updatePassword(@RequestParam("token") String token,
			@RequestBody UsuarioDTO usuarioDTO) {
		return usuarioServiceImpl.updatePassword(token, usuarioDTO);
	}
	
	@Operation(summary = "Operación que permite editar la contraseña de usuario")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se ha creado satisfactoriamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })

	@PutMapping("/edit-password")
	public ResponseEntity<ResponseDTO> editPassword(@RequestBody UsuarioDTO usuarioDTO) {
	    return usuarioServiceImpl.editPassword(usuarioDTO);
	}

	@Operation(summary = "Operacion que permite consultar un usuario a partir de un id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
	@GetMapping({ "/{id}" })
	public ResponseEntity<ResponseDTO> getUsuarioId(@PathVariable Integer id) {
		return this.usuarioServiceImpl.findUsuarioById(id);
	}

	@Operation(summary = "Operación que permite recuperar la contraseña de un usuario")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Correo enviado exitosamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
	@PostMapping("/recover-password")
	public ResponseEntity<ResponseDTO> recoverPassword(@RequestParam String usuario) {
		return usuarioServiceImpl.recoverPassword(usuario);
	}
	
	@Operation(summary = "Operacion que permite consultar los usuarios")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
 @GetMapping({"/all"})
  public ResponseEntity<ResponseDTO> getAllUsuarios() {
    return this.usuarioServiceImpl.findAll();
  }
}
