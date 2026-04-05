package com.caravela21.palpiteiro.api.exceptions;

public class UnauthorizedPoolOperationException extends RuntimeException {
    public UnauthorizedPoolOperationException(String message) {
        super(message);
    }
}

