package com.gestion.plus.api.controller;

import com.gestion.plus.api.service.IMensajeMasivoService;
import com.gestion.plus.commons.dtos.ArchivoMensajeDTO;
import com.gestion.plus.commons.dtos.ButtonMensajeDTO;
import com.gestion.plus.commons.dtos.MensajeMasivoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.UsuarioEntity;
import com.gestion.plus.commons.entities.VigenciaUsuarioEntity;
import com.gestion.plus.commons.repositories.VigenciaUsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({ "/api/v1/MensajeMasivo" })
@CrossOrigin(origins = { "*" }, methods = { RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT })
@RequiredArgsConstructor
public class MensajeMasivoController {
	
	private final IMensajeMasivoService mensajeMasivoService;
    private final VigenciaUsuarioRepository vigenciaUsuarioRepository;

    @Operation(summary = "Operación que permite crear un mensaje masivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se crea exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Petición con errores", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))})
    })
    @PostMapping
    public ResponseEntity<ResponseDTO> sendMensajeMasivo(@RequestBody MensajeMasivoRequest request,
                                                          @RequestHeader("Authorization") String token) {
        String llave = token.replace("Bearer ", "").trim();

        Optional<VigenciaUsuarioEntity> vigenciaOptional = vigenciaUsuarioRepository.findByLlave(llave);
        if (vigenciaOptional.isEmpty() || !vigenciaOptional.get().isTokenVigente()) {
            return ResponseEntity.status(401).body(ResponseDTO.builder()
                    .success(false)
                    .message("Token inválido o expirado")
                    .code(401)
                    .build());
        }

        UsuarioEntity usuario = vigenciaOptional.get().getUsuario();
        if (request.getMensajeMasivo().getTitulo() == null || request.getMensajeMasivo().getMensaje() == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .success(false)
                    .message("El título y mensaje son obligatorios")
                    .code(400)
                    .build());
        }

        return mensajeMasivoService.sendMensajeMasivo(
                request.getMensajeMasivo(),
                request.getArchivos(),
                request.getBotones(),
                usuario
        );
    }
     
    @Data
    @ToString
    static class MensajeMasivoRequest {
        private MensajeMasivoDTO mensajeMasivo;
        private List<ArchivoMensajeDTO> archivos;
        private List<ButtonMensajeDTO> botones;
    }
}
