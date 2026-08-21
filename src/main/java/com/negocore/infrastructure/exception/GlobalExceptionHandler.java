package com.negocore.infrastructure.exception;

import com.negocore.domain.exception.BadRequestException;
import com.negocore.infrastructure.constants.InfrastructureConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(),
                        error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(InfrastructureConstants.MSG_INVALID_DATA, InfrastructureConstants.BAD_REQUEST, errors));

    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(InfrastructureConstants.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), InfrastructureConstants.BAD_REQUEST));
    }

}