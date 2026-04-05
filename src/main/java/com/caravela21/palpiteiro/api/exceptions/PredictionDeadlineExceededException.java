package com.caravela21.palpiteiro.api.exceptions;

public class PredictionDeadlineExceededException extends RuntimeException {
    public PredictionDeadlineExceededException(String message) {
        super(message);
    }
}

