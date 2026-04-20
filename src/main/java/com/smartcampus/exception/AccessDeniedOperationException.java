package com.smartcampus.exception;

public class AccessDeniedOperationException extends RuntimeException {

    public AccessDeniedOperationException(String message) {
        super(message);
    }
}
