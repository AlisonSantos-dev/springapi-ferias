package com.alisonsantos.springapiferias.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Object id) {
        super("Recurso nao encontrado. Id: " + id);
    }
}