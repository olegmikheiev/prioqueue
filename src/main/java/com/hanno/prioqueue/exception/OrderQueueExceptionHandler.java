package com.hanno.prioqueue.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class OrderQueueExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidOrderParametersException.class)
    public ResponseEntity<Object> handleInvalidOrderParametersException(InvalidOrderParametersException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateClientOrderException.class)
    public ResponseEntity<Object> handleDuplicateClientOrderException(DuplicateClientOrderException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

}
