package com.hanno.prioqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DuplicateClientOrderException extends RuntimeException {

    public DuplicateClientOrderException(String message) {
        super(message);
    }

}
