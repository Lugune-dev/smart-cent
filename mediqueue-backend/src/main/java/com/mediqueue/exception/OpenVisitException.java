package com.mediqueue.exception;

import org.springframework.http.HttpStatus;

public class OpenVisitException extends ApiException {
    public OpenVisitException() {
        super(HttpStatus.CONFLICT, "open_visit", "You have a visit happening now. Finish your visit before deleting your account.");
    }
}
