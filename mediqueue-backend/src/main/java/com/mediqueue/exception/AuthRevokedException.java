package com.mediqueue.exception;

import org.springframework.http.HttpStatus;

public class AuthRevokedException extends ApiException {
    public AuthRevokedException() {
        super(HttpStatus.UNAUTHORIZED, "auth_revoked", "Firebase ID token is invalid, expired, or missing.");
    }
}
