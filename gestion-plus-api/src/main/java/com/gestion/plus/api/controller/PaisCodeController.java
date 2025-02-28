package com.gestion.plus.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.plus.api.services.impl.PaisCodeServiceImpl;
import com.gestion.plus.commons.dtos.PaisCodeDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/v1/PaisCode"})
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequiredArgsConstructor
public class PaisCodeController {
	
	private final PaisCodeServiceImpl paisCodeServiceImpl;
	  
	  @Operation(summary = "Operacion que permite crear un pais mas codigo")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })@PostMapping
	  public ResponseEntity<ResponseDTO> savePaisCode(@RequestBody PaisCodeDTO paisCodeDTO) {
	    return this.paisCodeServiceImpl.savePaisCode(paisCodeDTO);
	  }
	  
	  @Operation(summary = "Operacion que permite consultar los paises mas codigo")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })@GetMapping({"/all"})
	  public ResponseEntity<ResponseDTO> getAllPaisCode() {
	    return this.paisCodeServiceImpl.findAll();
	  }
	  
	  @Operation(summary = "Operacion que permite consultar un pais mas codigo por id")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })@GetMapping({"/{id}"})
	  public ResponseEntity<ResponseDTO> getPaisCodeById(@PathVariable Integer id) {
	    return this.paisCodeServiceImpl.findPaisCodeById(id);
	  }
	  
	  @Operation(summary = "Operacion que permite actualizar un pais mas codigo por id")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), })@PutMapping({"/{id}"})
	  public ResponseEntity<ResponseDTO> updatePaisCode(@PathVariable Integer id, @RequestBody PaisCodeDTO paisCodeDTO) {
	    return this.paisCodeServiceImpl.updatePaisCode(id, paisCodeDTO);
	  }
	  
	  @Operation(summary = "Operacion que permite eliminar un pais mas codigo por id")
	  @ApiResponses(value = {
				@ApiResponse(responseCode = "200", description = "Se consulta exitosamente", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis, el cliente no debe repetirla no sin antes hacer modificaciones", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
				@ApiResponse(responseCode = "500", description = "Se presento una condición inesperada que impidió completar la petición", content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }), }) @DeleteMapping({"/{id}"})
	  public ResponseEntity<ResponseDTO> deletePaisCode(@PathVariable Integer id) {
	    return this.paisCodeServiceImpl.deletePaisCode(id);
	  }

}
