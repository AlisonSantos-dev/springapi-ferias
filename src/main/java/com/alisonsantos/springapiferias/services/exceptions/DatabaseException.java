package com.alisonsantos.springapiferias.services.exceptions;

public class DatabaseException extends RuntimeException{

    public DatabaseException(String message) {
        super(message);
    }
}
