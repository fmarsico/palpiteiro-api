package com.caravela21.palpiteiro.api.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

/**
 * Global exception handler for handling various exceptions across the application.
 * This class uses @RestControllerAdvice to handle exceptions and return appropriate
 * HTTP status codes and messages.
 */
@RestControllerAdvice
public class ErrorAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        LOGGER.error("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errors.stream().map(ValidationErrors::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType() != null
                && invalidFormatException.getTargetType().isEnum()) {
            String field = invalidFormatException.getPath().isEmpty()
                    ? "unknown"
                    : invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1).getFieldName();

            List<String> acceptedValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .toList();

            String rejectedValue = String.valueOf(invalidFormatException.getValue());
            String message = "Invalid value '" + rejectedValue + "' for field '" + field + "'.";

            LOGGER.error("HttpMessageNotReadableException (enum): {} Accepted values: {}", message, acceptedValues);
            return ResponseEntity.badRequest().body(new InvalidEnumErrorDetails(
                    field,
                    rejectedValue,
                    acceptedValues,
                    message
            ));
        }

        LOGGER.error("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorDetails("Malformed request body."));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("EntityNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("UserNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ResponseEntity<?> handleUserAlreadyActivatedException(UserAlreadyActivatedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("UserAlreadyActivatedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<?> handleUserNotActivatedException(UserNotActivatedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("UserNotActivatedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> handleEntityExistsException(EntityExistsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("EntityExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
//        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
//        LOGGER.error("BadCredentialsException: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
//        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
//        LOGGER.error("AccessDeniedException: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
//    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("SQLIntegrityConstraintViolationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        LOGGER.error("BadRequestException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        ErrorDetails errorDetails = new ErrorDetails("An unexpected error occurred.");
        LOGGER.error("NullPointerException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

//    @ExceptionHandler(InternalAuthenticationServiceException.class)
//    public ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
//        Throwable cause = ex.getCause();
//        if (cause instanceof UserNotFoundException) {
//            return handleUserNotFoundException((UserNotFoundException) cause);
//        } else {
//            ErrorDetails errorDetails = new ErrorDetails("An internal authentication error occurred.");
//            LOGGER.error("InternalAuthenticationServiceException: {}", ex.getMessage(), ex);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
//        }
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ErrorDetails errorDetails = new ErrorDetails("An unexpected error occurred.");
        LOGGER.error("Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }


}
