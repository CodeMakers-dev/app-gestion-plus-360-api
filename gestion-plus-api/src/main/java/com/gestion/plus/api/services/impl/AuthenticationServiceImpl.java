package com.gestion.plus.api.services.impl;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.config.EncriptarDesancriptar;
import com.gestion.plus.api.config.JwtTokenUtil;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;
import com.gestion.plus.commons.entities.RolEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.maps.RolMapper;
import com.gestion.plus.commons.maps.UsuarioMapper;
import com.gestion.plus.commons.repositories.RolRepository;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements UserDetailsService {
	/**
	 * Inyección de la instancias que seran usadas en esta clase.
	 */
	private final EncriptarDesancriptar serviceEncriptacion;
	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final JwtTokenUtil jwtTokenUtil;

	/**
	 * Método que permite validar un objeto de tipo Usuario y generar un JWT.
	 */
	public ResponseEntity<ResponseDTO> autenticar(UsuarioDTO usuario) {
		log.info("Inicio método autenticar");
		ResponseDTO response;

		if (usuario == null || usuario.getUsuario() == null || usuario.getPassword() == null
				|| usuario.getUsuario().isEmpty() || usuario.getPassword().isEmpty()) {
			response = Utils.mapearRespuesta(Constantes.MESSAGE_VALIDATION_DATA, HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		Optional<UsuarioEntity> responseUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
		if (!responseUsuario.isPresent()) {
			return new ResponseEntity<>(
					Utils.mapearRespuesta(Constantes.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED.value()),
					HttpStatus.UNAUTHORIZED);
		}

		log.info("Usuario encontrado");
		
		if (!responseUsuario.get().getActivo()) { 
		    return new ResponseEntity<>(
		            Utils.mapearRespuesta("El usuario está inactivo", HttpStatus.UNAUTHORIZED.value()), 
		            HttpStatus.UNAUTHORIZED);
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(usuario.getPassword(), responseUsuario.get().getPassword())) {
			return new ResponseEntity<>(
					Utils.mapearRespuesta(Constantes.PASSWORD_ERROR, HttpStatus.UNAUTHORIZED.value()),
					HttpStatus.UNAUTHORIZED);
		}

		List<RolEntity> lstRol = rolRepository.findRolPorUsuario(responseUsuario.get().getId());
		UsuarioDTO usuarioResponse = UsuarioMapper.INSTANCE.entityToDto(responseUsuario.get());
		usuarioResponse.setLstRol(RolMapper.INSTANCE.beanListToDtoList(lstRol));

		UserDetails userDetails = new User(usuario.getUsuario(), responseUsuario.get().getPassword(),
				new ArrayList<>());
		String token = jwtTokenUtil.generateToken(userDetails);
		usuarioResponse.setToken(token);

		response = Utils.mapearRespuesta(Constantes.SUCCESSFUL, HttpStatus.OK.value(), usuarioResponse);
		log.info("Fin método autenticar");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UsuarioEntity> usuario = usuarioRepository.findByUsuario(username);
		if (!usuario.isPresent()) {
			throw new UsernameNotFoundException("Usuario no encontrado: " + username);
		}
		return new User(username, usuario.get().getPassword(), new ArrayList<>());
	}
}
