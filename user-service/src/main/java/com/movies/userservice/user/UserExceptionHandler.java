package com.movies.userservice.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<String> whenNotConnected(){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Keycloak server is not available");
    }
}
