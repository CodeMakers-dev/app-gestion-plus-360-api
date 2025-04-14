package com.gestion.plus.api.service;

import org.springframework.http.ResponseEntity;

import com.gestion.plus.commons.dtos.DocumentoRequestDTO;
import com.gestion.plus.commons.dtos.ImageRequestDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.VideoRequestDTO;

public interface IWhatsAppService {
	
	ResponseEntity<ResponseDTO> sendTemplateWithImage(ImageRequestDTO request);
	
	ResponseEntity<ResponseDTO> sendTemplateWithVideo(VideoRequestDTO request);
	
	ResponseEntity<ResponseDTO> sendTemplateWithDocument(DocumentoRequestDTO request);
}
