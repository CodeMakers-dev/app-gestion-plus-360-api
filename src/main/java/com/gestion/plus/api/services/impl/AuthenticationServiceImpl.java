package com.gestion.plus.api.services.impl;

import java.util.ArrayList;
import java.util.Date;
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

import com.gestion.plus.api.config.JwtTokenUtil;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.UsuarioDTO;
import com.gestion.plus.commons.entities.RolEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.entities.VigenciaUsuarioEntity;
import com.gestion.plus.commons.maps.RolMapper;
import com.gestion.plus.commons.maps.UsuarioMapper;
import com.gestion.plus.commons.repositories.RolRepository;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.repositories.VigenciaUsuarioRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements UserDetailsService {
	
	    
	    private final UsuarioRepository usuarioRepository;
	    private final RolRepository rolRepository;
	    private final JwtTokenUtil jwtTokenUtil;
	    private final VigenciaUsuarioRepository vigenciaUsuarioRepository;

	    /**
	     * Método que permite validar un objeto de tipo Usuario y generar un JWT.
	     */
	    @Transactional
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

	        // Guardar el token en VigenciaUsuario
	        VigenciaUsuarioEntity vigenciaUsuario = new VigenciaUsuarioEntity();
	        vigenciaUsuario.setLlave(token);
	        vigenciaUsuario.setUsuario(responseUsuario.get()); // Usar la relación directa con UsuarioEntity
	        vigenciaUsuario.setFechaVigencia(calcularFechaVigencia()); // Calcular la fecha de vigencia
	        vigenciaUsuario.setUsuarioCreacion(responseUsuario.get().getUsuario());
	        try {
	            vigenciaUsuarioRepository.save(vigenciaUsuario);
	        } catch (Exception e) {
	            log.error("Error al guardar el token en VigenciaUsuario: ", e);
	            return new ResponseEntity<>(
	                    Utils.mapearRespuesta("Error al guardar el token", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	                    HttpStatus.INTERNAL_SERVER_ERROR);
	        }

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

	 // Método para calcular la fecha de vigencia (5 días)
	    private Date calcularFechaVigencia() {
	        long currentTimeMillis = System.currentTimeMillis();
	        long expirationTimeMillis = currentTimeMillis + (5L * 24 * 60 * 60 * 1000); 
	        return new Date(expirationTimeMillis);
	    }
	}

