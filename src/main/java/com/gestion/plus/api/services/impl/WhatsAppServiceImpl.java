package com.gestion.plus.api.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gestion.plus.api.service.IWhatsAppService;
import com.gestion.plus.commons.dtos.DocumentoRequestDTO;
import com.gestion.plus.commons.dtos.ImageRequestDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.dtos.VideoRequestDTO;
import com.gestion.plus.commons.enums.MediaTypeEnum;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppServiceImpl implements IWhatsAppService {

	@Value("${whatsapp.api.url}")
	private String whatsappApiUrl;

	@Value("${whatsapp.phone.number-id}")
	private String phoneNumberId;

	@Value("${whatsapp.access.token}")
	private String accessToken;

	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public ResponseEntity<ResponseDTO> sendTemplateWithImage(ImageRequestDTO request) {
		ResponseEntity<ResponseDTO> finalResponse = null;
		for (String recipient : request.getTo()) {
			finalResponse = sendTemplateWithMediaAndBody(recipient, request.getTemplateName(),
					request.getLanguageCode(), MediaTypeEnum.IMAGE, request.getImageUrl(), request.getBodyText1(),
					request.getBodyText2());
		}
		return finalResponse;
	}

	@Override
	public ResponseEntity<ResponseDTO> sendTemplateWithVideo(VideoRequestDTO request) {
		ResponseEntity<ResponseDTO> finalResponse = null;
		for (String recipient : request.getTo()) {
			finalResponse = sendTemplateWithMediaAndBody(recipient, request.getTemplateName(),
					request.getLanguageCode(), MediaTypeEnum.VIDEO, request.getVideoUrl(), request.getBodyText1(),
					request.getBodyText2());
		}
		return finalResponse;
	}

	@Override
	public ResponseEntity<ResponseDTO> sendTemplateWithDocument(DocumentoRequestDTO request) {
		ResponseEntity<ResponseDTO> finalResponse = null;
		for (String recipient : request.getTo()) {
			finalResponse = sendTemplateWithMediaAndBody(recipient, request.getTemplateName(),
					request.getLanguageCode(), MediaTypeEnum.DOCUMENT, request.getPdfUrl(), request.getBodyText1(),
					request.getBodyText2());
		}
		return finalResponse;
	}

	private ResponseEntity<ResponseDTO> sendTemplateWithMediaAndBody(String to, String templateName,
			String languageCode, MediaTypeEnum mediaType, String mediaUrl, String bodyText1, String bodyText2) {

		String url = whatsappApiUrl + phoneNumberId + "/messages";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		Map<String, Object> body = buildRequestBodyWithMediaAndBody(to, templateName, languageCode, mediaType, mediaUrl,
				bodyText1, bodyText2);
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		try {
			log.info("Enviando mensaje a WhatsApp a {} con template {}", to, templateName);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(ResponseMessages.SENT_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).response(response.getBody()).build();

			return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

		} catch (Exception e) {
			log.error("Error enviando mensaje a WhatsApp: {}", e.getMessage(), e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false)
					.message(ResponseMessages.SEND_ERROR + ": " + e.getMessage()).code(HttpStatus.BAD_REQUEST.value())
					.response(null).build();

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}
	}

	private Map<String, Object> buildRequestBodyWithMediaAndBody(String to, String templateName, String languageCode,
			MediaTypeEnum mediaType, String mediaUrl, String bodyText1, String bodyText2) {

		Map<String, Object> body = new HashMap<>();
		body.put("messaging_product", "whatsapp");
		body.put("to", to);
		body.put("type", "template");

		Map<String, Object> template = new HashMap<>();
		template.put("name", templateName);

		Map<String, String> language = new HashMap<>();
		language.put("code", languageCode);
		template.put("language", language);

		List<Map<String, Object>> components = new java.util.ArrayList<>();

		Map<String, Object> headerComponent = new HashMap<>();
		headerComponent.put("type", "header");
		Map<String, Object> mediaParam = new HashMap<>();
		mediaParam.put("type", mediaType.getType());
		Map<String, String> media = new HashMap<>();
		media.put("link", mediaUrl);
		mediaParam.put(mediaType.getType(), media);
		headerComponent.put("parameters", List.of(mediaParam));
		components.add(headerComponent);

		Map<String, Object> bodyComponent = new HashMap<>();
		bodyComponent.put("type", "body");

		Map<String, Object> textParam1 = new HashMap<>();
		textParam1.put("type", "text");
		textParam1.put("text", bodyText1);

		Map<String, Object> textParam2 = new HashMap<>();
		textParam2.put("type", "text");
		textParam2.put("text", bodyText2);

		bodyComponent.put("parameters", List.of(textParam1, textParam2));
		components.add(bodyComponent);

		template.put("components", components);
		body.put("template", template);

		return body;
	}
}