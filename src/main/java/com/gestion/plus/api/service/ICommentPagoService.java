package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.CommentPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;

public interface ICommentPagoService {

	ResponseEntity<ResponseDTO> saveComentario(CommentPagoDTO commentPagoDTO);
	
	ResponseEntity<ResponseDTO> findComentarioById(Integer idPago);
	
	ResponseEntity<ResponseDTO> deleteComentario(Integer Id);
}
