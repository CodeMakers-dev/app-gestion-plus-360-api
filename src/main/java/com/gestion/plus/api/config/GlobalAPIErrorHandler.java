package com.gestion.plus.api.config;

import com.gestion.plus.commons.dtos.ResponseDTO;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalAPIErrorHandler {
  private static final Logger log = LoggerFactory.getLogger(com.gestion.plus.api.config.GlobalAPIErrorHandler.class);
  
  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<ResponseDTO> notFoundError(RuntimeException ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status((HttpStatusCode)HttpStatus.CONFLICT).body(ResponseDTO.builder().message(ex.getMessage())
        .code(Integer.valueOf(HttpStatus.CONFLICT.value())).success(Boolean.valueOf(false)).build());
  }
  
  @ExceptionHandler({Exception.class})
  public ResponseEntity<ResponseDTO> errorInternal(Exception ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status((HttpStatusCode)HttpStatus.CONFLICT).body(ResponseDTO.builder().message(ex.getMessage())
        .code(Integer.valueOf(HttpStatus.CONFLICT.value())).success(Boolean.valueOf(false)).build());
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<ResponseDTO> errorRequest(RuntimeException ex) {
    log.error(ex.getMessage());
    ResponseDTO response = new ResponseDTO();
    response.setMessage(ex.getMessage());
    response.setCode(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<ResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
    ResponseDTO response = new ResponseDTO();
    if (ex.getBindingResult().getAllErrors().size() == 1) {
      ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = (response.getMessage() != null) ? (response.getMessage() + "," + response.getMessage()) : error.getDefaultMessage();
            log.error(errorMessage);
            response.setMessage(errorMessage.replace("{0}", fieldName));
            response.setCode(Integer.valueOf(HttpStatus.BAD_REQUEST.value()));
          });
    } else {
      response.setMessage("Los campos enviados no cumplen con los tama");
      response.setCode(Integer.valueOf(HttpStatus.BAD_REQUEST.value()));
    } 
    return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
  }
  
  @ExceptionHandler({AsyncRequestTimeoutException.class})
  public ResponseEntity<ResponseDTO> errorRequestTimeout(AsyncRequestTimeoutException ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status((HttpStatusCode)HttpStatus.CONFLICT).body(ResponseDTO.builder().message(ex.getMessage())
        .code(Integer.valueOf(HttpStatus.CONFLICT.value())).success(Boolean.valueOf(false)).build());
  }
}
