package com.caravela21.palpiteiro.api.exceptions;

import org.springframework.validation.FieldError;

/**
 * Record representing validation errors.
 * This record is used to encapsulate validation error details for fields in the API responses.
 */
public record ValidationErrors(String field, String message) {

    /**
     * Constructor to create a ValidationErrors record from a FieldError object.
     *
     * @param error the FieldError object containing the field and default message
     */
    public ValidationErrors(FieldError error){
        this(error.getField(), error.getDefaultMessage());
    }
}
