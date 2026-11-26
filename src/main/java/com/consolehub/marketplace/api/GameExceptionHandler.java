package com.consolehub.marketplace.api;

import java.time.Instant;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.consolehub.marketplace.api.dto.ApiError;
import com.consolehub.marketplace.exceptions.GameConflictException;
import com.consolehub.marketplace.exceptions.GameNotFoundException;
import com.consolehub.marketplace.exceptions.GameValidationException;

@RestControllerAdvice 
public class GameExceptionHandler {
    
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiError> handleInvalidArgument(
        GameNotFoundException exception){
            return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler ({GameConflictException.class,
                        OptimisticLockingFailureException.class})
    public ResponseEntity<ApiError> handleConflict(
        RuntimeException exception){
            return error(HttpStatus.CONFLICT, 
                "Game was modified  or is in a conflicting state"
            );
    }

    // TODO: GameValidationException is not thrown anywhere yet. The domain currently
    // throws IllegalArgumentException. Decide whether to switch the domain over to it.
    @ExceptionHandler ({GameValidationException.class,
                        IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> handleValidation(RuntimeException exception){
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler (IllegalStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(IllegalStateException exception){
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleRequestValidation(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError ->
            fieldError.getField() + " : " +
            fieldError.getDefaultMessage()
        )
        .findFirst()
        .orElse("Invalid Request");

        return error(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<ApiError> error(
        HttpStatus status,
        String message) {

            return ResponseEntity.status(status).body(
                new ApiError(
                    Instant.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message
                )
            );
    }
}
