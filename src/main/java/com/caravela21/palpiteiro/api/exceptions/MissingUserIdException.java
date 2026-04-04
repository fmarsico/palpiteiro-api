package com.caravela21.palpiteiro.api.exceptions;

public class MissingUserIdException extends RuntimeException {
    public MissingUserIdException(String message) {
        super(message);
    }
}

