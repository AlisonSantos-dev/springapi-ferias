package com.alisonsantos.springapiferias.resources.exceptions;

import com.alisonsantos.springapiferias.services.exceptions.PeriodoSobrepostoException;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ResourceExceptionHandler {

    // 404 - recurso (por id) nao encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), "Recurso nao encontrado",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // 409 - conflito de regra de negocio (datas sobrepostas)
    @ExceptionHandler(PeriodoSobrepostoException.class)
    public ResponseEntity<StandardError> periodoSobreposto(PeriodoSobrepostoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), "Conflito de periodo",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // 400 - dados de entrada invalidos (ex.: dataInicio depois de dataFim)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), "Dados invalidos",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
