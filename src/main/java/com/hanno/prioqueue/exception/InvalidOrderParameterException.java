package com.hanno.prioqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderParameterException extends Exception {

    public InvalidOrderParameterException(String message) {
        super(message);
    }

}
