package com.game.exeption_handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class PlayerExceptionHandling {

    @ExceptionHandler
    public ResponseEntity<Exception> findByIdExceptionHandle(Exception exception) {
        if (exception instanceof NoSuchElementException) {
            return new ResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
