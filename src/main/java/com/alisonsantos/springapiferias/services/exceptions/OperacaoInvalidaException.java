package com.alisonsantos.springapiferias.services.exceptions;

public class OperacaoInvalidaException extends RuntimeException {

    public OperacaoInvalidaException(String message) {
        super(message);
    }
}
