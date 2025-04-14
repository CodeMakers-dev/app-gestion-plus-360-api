package com.gestion.plus.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.gestion.plus.api.services.impl.WhatsAppServiceImpl;
import com.gestion.plus.commons.dtos.DocumentoRequestDTO;
import com.gestion.plus.commons.dtos.ImageRequestDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.VideoRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = { "*" }, methods = { RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT })
@RequiredArgsConstructor
public class WhatsAppController {

	private final WhatsAppServiceImpl whatsAppServiceImpl;


	 @Operation(summary = "Operación que permite enviar un mensaje de WhatsApp con plantilla de imagen")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Mensaje enviado exitosamente", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "La petición tiene errores de sintaxis o datos faltantes", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) })
	    })
	    @PostMapping("/send-template-image")
	    public ResponseEntity<ResponseDTO> sendTemplateWithImage(@RequestBody ImageRequestDTO request) {
	        return whatsAppServiceImpl.sendTemplateWithImage(request);
	    }
	 
	 @Operation(summary = "Operación que permite enviar un mensaje de WhatsApp con plantilla de video")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Mensaje enviado exitosamente", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "La petición tiene errores de sintaxis o datos faltantes", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) })
	    })
	    @PostMapping("/send-template-video")
	    public ResponseEntity<ResponseDTO> sendTemplateWithVideo(@RequestBody VideoRequestDTO request) {
	        return whatsAppServiceImpl.sendTemplateWithVideo(request);
	    }
	 
	 @Operation(summary = "Operación que permite enviar un mensaje de WhatsApp con plantilla de documento")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Mensaje enviado exitosamente", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "La petición tiene errores de sintaxis o datos faltantes", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor", content = {
	            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) })
	    })
	    @PostMapping("/send-template-documento")
	    public ResponseEntity<ResponseDTO> sendTemplateWithDocument(@RequestBody DocumentoRequestDTO request) {
	        return whatsAppServiceImpl.sendTemplateWithDocument(request);
	    }
}