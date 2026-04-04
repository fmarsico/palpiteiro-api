package com.caravela21.palpiteiro.api.exceptions;

public class UserNotActivatedException extends RuntimeException {
    public UserNotActivatedException(String message) {
        super(message);
    }
}