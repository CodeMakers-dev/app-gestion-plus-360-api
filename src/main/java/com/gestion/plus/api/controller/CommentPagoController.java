package com.gestion.plus.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.plus.api.services.impl.CommentPagoServiceImpl;
import com.gestion.plus.commons.dtos.CommentPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/v1/comentario"})
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequiredArgsConstructor
public class CommentPagoController {
	
	private final CommentPagoServiceImpl commentPagoServiceImpl;
	
	@Operation(summary = "Operacion que permite crear comentario de pago")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
	
	@PostMapping("/guardar")
	  public ResponseEntity<ResponseDTO> saveComentario(@RequestBody CommentPagoDTO commentPagoDTO) {
	    return this.commentPagoServiceImpl.saveComentario(commentPagoDTO);
	  }
	
	@Operation(summary = "Operacion que permite obtiene los comentarios de un pago por ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })
	@GetMapping("/pago/{idPago}")
	public ResponseEntity<ResponseDTO> findComentarioById(@PathVariable Integer idPago) {
		return this.commentPagoServiceImpl.findComentarioById(idPago);
	}
}
