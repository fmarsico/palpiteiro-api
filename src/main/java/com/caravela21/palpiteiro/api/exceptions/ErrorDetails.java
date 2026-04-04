package com.caravela21.palpiteiro.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class representing the details of an error.
 * This class is used to encapsulate error messages that are returned in the API responses.
 */
@Data
@AllArgsConstructor
public class ErrorDetails {

    private String message;

}
