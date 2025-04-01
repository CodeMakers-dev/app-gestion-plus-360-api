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
import com.gestion.plus.commons.dtos.RolDTO;
import com.gestion.plus.commons.entities.CiudadEntity;
import com.gestion.plus.commons.entities.DepartamentoEntity;
import com.gestion.plus.commons.entities.PaisEntity;
import com.gestion.plus.commons.entities.PersonaEntity;
import com.gestion.plus.commons.entities.RolEntity;
import com.gestion.plus.commons.entities.TipoDocumentoEntity;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.entities.UsuarioRolEntity;
import com.gestion.plus.commons.entities.VigenciaUsuarioEntity;
import com.gestion.plus.commons.maps.PersonaMapper;
import com.gestion.plus.commons.repositories.MensajeRepository;
import com.gestion.plus.commons.repositories.PersonaRepository;
import com.gestion.plus.commons.repositories.RolRepository;
import com.gestion.plus.commons.repositories.UsuarioRepository;
import com.gestion.plus.commons.repositories.UsuarioRolRepository;
import com.gestion.plus.commons.repositories.VigenciaUsuarioRepository;
import com.gestion.plus.commons.utils.ResponseMessages;

import jakarta.transaction.Transactional;
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
	private final RolRepository rolRepository;
	private final UsuarioRolRepository usuarioRolRepository;

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
			
			if (personaDTO.getRol() != null) {
			    Integer rolId = personaDTO.getRol().getId();
			    RolEntity rol = rolRepository.findById(rolId) 
			        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
			    usuario.setRol(rol);
			} else {
			    throw new RuntimeException("El ID del rol es obligatorio");
			}

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
			String mensaje = "<!DOCTYPE html>" +
				    "<html>" +
				    "<head>" +
				    "<style>" +
				    "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; text-align: center; }" +
				    ".container { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); max-width: 500px; margin: auto; padding: 20px; }" +
				    ".logo { width: 150px; margin-bottom: 20px; }" +
				    "h2 { color: #333333; font-size: 20px; }" +
				    "p { font-size: 16px; color: #666666; line-height: 1.5; }" +
				    "a { display: inline-block; padding: 10px 20px; margin-top: 10px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; }" +
				    "a:hover { background-color: #0056b3; }" +
				    ".link-container { margin-top: 10px; font-size: 14px; word-wrap: break-word; }" +
				    ".footer { margin-top: 20px; font-size: 14px; color: #888888; }" +
				    "</style>" +
				    "</head>" +
				    "<body>" +
				    "<div class='container'>" +
				    "<img src='https://lh3.googleusercontent.com/a/ACg8ocI8YnNsVJjvY_A40BjiJJN9OxiDpCvbC-TpHDfYO_cC_u59P9c=s288-c-no' alt='Logo' class='logo'>" +  // Reemplaza con tu logo
				    "<h2>Bienvenido</h2>" +
				    "<p>Para completar tu registro, necesitas crear tu contraseña.</p>" +
				    "<p>Haz clic en el siguiente enlace para configurar tu contraseña:</p>" +
				    "<a href='" + resetLink + "'>Crear contraseña</a>" +
				    "<div class='link-container'>" +
				    "<p>Si el enlace no funciona, copia y pega la siguiente URL en tu navegador:</p>" +
				    "<li href='" + resetLink + "'>" + resetLink + "</li>" +
				    "</div>" +
				    "<p>Por tu seguridad, tu contraseña debe cumplir estos requisitos:</p>" +
				    "<ul style='text-align: left; display: inline-block; margin-top: 10px;'>" +
				    "<li>Debe tener al menos 8 caracteres.</li>" +
				    "<li>No debe contener tu nombre, apellido o fecha de nacimiento.</li>" +
				    "<li>No puede ser igual a la respuesta de seguridad.</li>" +
				    "</ul>" +
				    "<p class='footer'>Equipo de Soporte</p>" +
				    "</div>" +
				    "</body>" +
				    "</html>";
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
	
    @Transactional
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

			log.info("Persona y usuario eliminados correctamente.");

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
	
	public ResponseEntity<ResponseDTO> updateImagenPersona(Integer id, byte[] imagen) {
        log.info("Inicio método actualizar imagen de Persona con ID: {}", id);

        try {
            Optional<PersonaEntity> personaOpt = personaRepository.findById(id);

            if (personaOpt.isEmpty()) {
                log.warn("No se encontró la persona con ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDTO.builder()
                                .success(false)
                                .message("Persona no encontrada")
                                .code(HttpStatus.NOT_FOUND.value())
                                .build());
            }

            PersonaEntity persona = personaOpt.get();
            persona.setImagen(imagen);
            personaRepository.save(persona);

            log.info("Imagen de Persona con ID: {} actualizada correctamente", id);
            return ResponseEntity.ok(ResponseDTO.builder()
                    .success(true)
                    .message("Imagen actualizada correctamente")
                    .code(HttpStatus.OK.value())
                    .build());

        } catch (Exception e) {
            log.error("Error al actualizar la imagen de Persona con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message("Error al actualizar la imagen")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }
}
