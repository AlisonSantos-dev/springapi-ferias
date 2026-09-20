package com.alisonsantos.springapiferias.services.exceptions;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String message) {
        super(message);
    }
}
