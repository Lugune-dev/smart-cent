package com.mediqueue.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends ApiException {
    public InvalidInputException(String message) {
        super(HttpStatus.BAD_REQUEST, "invalid_input", message != null ? message : "Malformed request body or invalid parameters.");
    }
}
