package com.gestion.plus.api.services.impl;

import java.util.List;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gestion.plus.api.service.ICommentPagoService;
import com.gestion.plus.commons.dtos.CommentPagoDTO;
import com.gestion.plus.commons.dtos.ResponseDTO;
import com.gestion.plus.commons.entities.CommentPagoEntity;
import com.gestion.plus.commons.entities.PagosEntity;
import com.gestion.plus.commons.maps.CommentPagoMapper;
import com.gestion.plus.commons.maps.PagosMapper;
import com.gestion.plus.commons.repositories.CommentPagoRepository;
import com.gestion.plus.commons.repositories.PagosRepository;
import com.gestion.plus.commons.utils.Constantes;
import com.gestion.plus.commons.utils.ResponseMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentPagoServiceImpl  implements ICommentPagoService{
	
	private final CommentPagoRepository commentPagoRepository;

	private final PagosRepository pagosRepository;
	
	public ResponseEntity<ResponseDTO> saveComentario(CommentPagoDTO commentPagoDTO) {
		log.info("Inicio metodo guardar comentario de pago");
		try {
			ResponseDTO responseDTO;
			Optional<PagosEntity> pagoOptional = this.pagosRepository
					.findById(commentPagoDTO.getPagos().getId());
			if (pagoOptional.isPresent()) {
				CommentPagoEntity savedComentario = (CommentPagoEntity) this.commentPagoRepository
						.save(CommentPagoMapper.INSTANCE.dtoToEntity(commentPagoDTO));
				CommentPagoDTO lastInsertComentario = CommentPagoMapper.INSTANCE.entityToDto(savedComentario);
				lastInsertComentario.setPagos(PagosMapper.INSTANCE.entityToDto(pagoOptional.get()));
				log.info("Fin metodo guardar comentario pago");
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(true))
						.message(ResponseMessages.SAVED_SUCCESSFULLY).code(Integer.valueOf(HttpStatus.CREATED.value()))
						.response(lastInsertComentario).build();
			} else {
				responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false)).message(Constantes.PAY_ERROR)
						.code(Integer.valueOf(HttpStatus.NOT_FOUND.value())).response(null).build();
			}
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder().success(Boolean.valueOf(false))
					.message(ResponseMessages.SAVE_ERROR).code(Integer.valueOf(HttpStatus.NOT_FOUND.value()))
					.response(null).build();
			return ResponseEntity.status(responseDTO.getCode().intValue()).body(responseDTO);
		}
	}
	

    public ResponseEntity<ResponseDTO> findComentarioById(Integer idPago) {
        log.info("Inicio método obtener comentarios por ID de pago");

        Optional<PagosEntity> pagosOpt = pagosRepository.findById(idPago);
        if (pagosOpt.isPresent()) {
            List<CommentPagoEntity> comentarios = commentPagoRepository.findByPagos(pagosOpt.get());
            List<CommentPagoDTO> commentPagoDTO = CommentPagoMapper.INSTANCE.beanListToDtoList(comentarios);

            return ResponseEntity.ok(ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.COMMENT_FOUND)
                    .code(HttpStatus.OK.value())
                    .response(commentPagoDTO)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.PAY_NOT_FOUND)
                            .code(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }
}
