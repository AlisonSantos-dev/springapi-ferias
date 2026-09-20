package com.alisonsantos.springapiferias.resources.exceptions;

import com.alisonsantos.springapiferias.services.exceptions.*;
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

    // 403 - tentativa de mexer em recurso que nao pertence ao colaborador logado
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<StandardError> acessoNegado(AcessoNegadoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(Instant.now(), status.value(), "Acesso negado",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // 409 - operacao nao permitida no estado atual (ex.: cancelar um periodo ja aprovado)
    @ExceptionHandler(OperacaoInvalidaException.class)
    public ResponseEntity<StandardError> operacaoInvalida(OperacaoInvalidaException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), "Operacao invalida",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // 400 - falha de integridade referencial no banco
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), "Erro de banco de dados",
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
