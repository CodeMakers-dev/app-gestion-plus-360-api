package com.gestion.plus.api.services.impl;

import java.util.Date;


import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.gestion.plus.api.service.IPersonaService;
import com.gestion.plus.commons.dtos.PersonaDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.CiudadEntity;
import com.gestion.plus.commons.entities.DepartamentoEntity;
import com.gestion.plus.commons.entities.PaisEntity;
import com.gestion.plus.commons.entities.PersonaEntity;
import com.gestion.plus.commons.entities.TipoDocumentoEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.entities.VigenciaUsuarioEntity;
import com.gestion.plus.commons.maps.PersonaMapper;
import com.gestion.plus.commons.repositories.MensajeRepository;
import com.gestion.plus.commons.repositories.PersonaRepository;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.repositories.VigenciaUsuarioRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaServiceImpl implements IPersonaService {

	private final PersonaRepository personaRepository;
	private final UsuarioRepository usuarioRepository;
	private final VigenciaUsuarioRepository vigenciaUsuarioRepository;
	private final EmailServiceImpl emailServiceImpl;
	private final MensajeRepository mensajeRepository;

	@Value("${app.url.reset-password}")
	private String resetPasswordUrl;

	public ResponseEntity<ResponseDTO> savePersona(PersonaDTO personaDTO) {
		log.info("Inicio metodo guardar persona");
		try {

			PersonaEntity personaEntity = PersonaMapper.INSTANCE.dtoToEntity(personaDTO);
			personaEntity.setFechaCreacion(new Date());
			personaEntity.setActivo(true);
			PersonaEntity savedEntity = personaRepository.save(personaEntity);
			UsuarioEntity usuario = new UsuarioEntity();
			usuario.setUsuario(personaDTO.getCorreo());
			usuario.setPersona(savedEntity);
			usuario.setActivo(true);
			usuario.setFechaCreacion(new Date());
			usuario.setUsuarioCreacion("Sistema");
			usuarioRepository.save(usuario);

			String llave = UUID.randomUUID().toString();
			Date fechaExpiracion = new Date(System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000));

			VigenciaUsuarioEntity vigencia = new VigenciaUsuarioEntity();
			vigencia.setLlave(llave);
			vigencia.setFechaVigencia(fechaExpiracion);
			vigencia.setUsuario(usuario);
			vigencia.setUsuarioCreacion("Sistema");
			vigencia.setFechaCreacion(new Date());
			vigencia.setActivo(true);
			vigenciaUsuarioRepository.save(vigencia);

			String resetLink = resetPasswordUrl + "?llave=" + llave;
			String mensaje = "Para crear tu contraseña, haz clic en el siguiente enlace: " + resetLink;
			emailServiceImpl.sendEmail(personaDTO.getCorreo(), "Activación de Cuenta", mensaje);

			log.info("Fin del metodo guardar persona");

			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(ResponseMessages.SAVED_SUCCESSFULLY)
					.code(HttpStatus.CREATED.value()).response(PersonaMapper.INSTANCE.entityToDto(savedEntity)).build();

			return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
		} catch (Exception e) {
			log.error("Error al guardar persona", e);
			ResponseDTO errorResponse = ResponseDTO.builder().success(false).message(ResponseMessages.SAVE_ERROR)
					.code(HttpStatus.BAD_REQUEST.value()).build();

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	public ResponseEntity<ResponseDTO> findAll() {
		ResponseDTO responseDTO;
		log.info("Inicio metod Obtener las personas");
		try {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(PersonaMapper.INSTANCE.beanListToDtoList(this.personaRepository.findAll())).build();
		} catch (Exception e) {
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.CONSULTING_ERROR).code(Integer.valueOf(HttpStatus.BAD_REQUEST.value()))
					.response(ResponseMessages.NO_RECORD_FOUND).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> findPersonaById(Integer id) {
		ResponseDTO responseDTO;
		log.info("Inicio del metodo para obtener las personas por id");
		Optional<PersonaEntity> personaOptional = this.personaRepository.findById(id);
		if (personaOptional.isPresent()) {
			PersonaDTO personaDTO = PersonaMapper.INSTANCE.entityToDto(personaOptional.get());
			responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
					.message(ResponseMessages.CONSULTED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.OK.value()))
					.response(personaDTO).build();
		} else {
			responseDTO = ResponseDTO.builder().code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.message("persona no encontrada para el Id: " + id).response(null).build();
		}
		return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
	}

	public ResponseEntity<ResponseDTO> updatePersona(Integer id, PersonaDTO personaDTO) {
		log.info("Inicio método actualizar persona");

		try {
			Optional<PersonaEntity> personaOptional = personaRepository.findById(id);
			if (personaOptional.isEmpty()) {
				log.warn("No se encontró la persona con ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ResponseDTO(false, "Persona no encontrada", HttpStatus.NOT_FOUND.value(), null));
			}

			PersonaEntity personaEntity = personaOptional.get();
			String correoOriginal = personaEntity.getCorreo();
			personaEntity.setNombre(personaDTO.getNombre());
			TipoDocumentoEntity tipoDocumento = new TipoDocumentoEntity();
			tipoDocumento.setId(personaDTO.getTipoDocumento().getId());
			personaEntity.setTipoDocumento(tipoDocumento);
			personaEntity.setNumeroDocumento(personaDTO.getNumeroDocumento());

			PaisEntity pais = new PaisEntity();
			pais.setId(personaDTO.getPais().getId());
			personaEntity.setPais(pais);

			DepartamentoEntity departamento = new DepartamentoEntity();
			departamento.setId(personaDTO.getDepartamento().getId());
			personaEntity.setDepartamento(departamento);

			CiudadEntity ciudad = new CiudadEntity();
			ciudad.setId(personaDTO.getCiudad().getId());
			personaEntity.setCiudad(ciudad);

			personaEntity.setDireccion(personaDTO.getDireccion());
			personaEntity.setActividadEconomica(personaDTO.getActividadEconomica());
			personaEntity.setTelefono(personaDTO.getTelefono());
			personaEntity.setCorreo(correoOriginal);

			personaEntity.setImagen(personaDTO.getImagen());
			personaEntity.setUsuarioModificacion("Sistema");
			personaEntity.setFechaModificacion(new Date());

			PersonaEntity updatedEntity = personaRepository.save(personaEntity);

			log.info("Fin del método actualizar persona");

			ResponseDTO responseDTO = new ResponseDTO(true, "Persona actualizada correctamente", HttpStatus.OK.value(),
					PersonaMapper.INSTANCE.entityToDto(updatedEntity));

			return ResponseEntity.ok(responseDTO);

		} catch (Exception e) {
			log.error("Error al actualizar persona", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseDTO(false, "Error al actualizar persona", HttpStatus.BAD_REQUEST.value(), null));
		}
	}

	public ResponseEntity<ResponseDTO> deletePersona(Integer id) {
		log.info("Inicio método eliminar persona");

		try {
			Optional<PersonaEntity> personaOptional = personaRepository.findById(id);
			if (personaOptional.isEmpty()) {
				log.warn("No se encontró la persona con ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ResponseDTO(false, "Persona no encontrada", HttpStatus.NOT_FOUND.value(), null));
			}
			PersonaEntity personaEntity = personaOptional.get();
			Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findByPersonaId(id);
			usuarioOptional.ifPresent(usuario -> {
				mensajeRepository.deleteByUsuarioId(usuario.getId());
				vigenciaUsuarioRepository.deleteByUsuarioId(usuario.getId());
				usuarioRepository.delete(usuario);
			});
			personaRepository.delete(personaEntity);

			log.info("Persona y usuario eliminados correctamente");

			return ResponseEntity
					.ok(new ResponseDTO(true, "Persona eliminada correctamente", HttpStatus.OK.value(), null));

		} catch (Exception e) {
			log.error("Error al eliminar persona", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseDTO(false, "Error al eliminar persona", HttpStatus.BAD_REQUEST.value(), null));
		}
	}

	public ResponseEntity<ResponseDTO> blockPersona(Integer id) {
		log.info("Inicio método bloquear persona");

		try {
			Optional<PersonaEntity> personaOptional = personaRepository.findById(id);
			if (personaOptional.isEmpty()) {
				log.warn("No se encontró la persona con ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ResponseDTO(false, "Persona no encontrada", HttpStatus.NOT_FOUND.value(), null));
			}

			PersonaEntity personaEntity = personaOptional.get();
			Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findByPersonaId(id);

			personaEntity.setActivo(false);
			personaEntity.setFechaModificacion(new Date());
			personaEntity.setUsuarioModificacion("Sistema");
			personaRepository.save(personaEntity);

			if (usuarioOptional.isPresent()) {
				UsuarioEntity usuarioEntity = usuarioOptional.get();
				usuarioEntity.setActivo(false);
				usuarioEntity.setFechaModificacion(new Date());
				usuarioEntity.setUsuarioModificacion("Sistema");
				usuarioRepository.save(usuarioEntity);
				log.info("Usuario con ID {} bloqueado correctamente", usuarioEntity.getId());
			}

			log.info("Persona y usuario bloqueados correctamente");

			return ResponseEntity.ok(
					new ResponseDTO(true, "Persona y usuario bloqueados correctamente", HttpStatus.OK.value(), null));

		} catch (Exception e) {
			log.error("Error al bloquear persona", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseDTO(false, "Error al bloquear persona", HttpStatus.BAD_REQUEST.value(), null));
		}
	}
}
