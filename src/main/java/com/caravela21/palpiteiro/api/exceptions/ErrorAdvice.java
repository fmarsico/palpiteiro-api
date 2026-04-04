package com.caravela21.palpiteiro.api.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

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
