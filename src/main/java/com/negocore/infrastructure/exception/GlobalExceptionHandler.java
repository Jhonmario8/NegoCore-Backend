package com.negocore.infrastructure.exception;

import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.ForbiddenException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.exception.UnauthorizedException;
import com.negocore.infrastructure.constants.InfrastructureConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(InfrastructureConstants.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(), InfrastructureConstants.UNAUTHORIZED));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(InfrastructureConstants.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage(), InfrastructureConstants.FORBIDDEN));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(InfrastructureConstants.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(), InfrastructureConstants.CONFLICT));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(InfrastructureConstants.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), InfrastructureConstants.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_INVALID_DATA,
                        InfrastructureConstants.BAD_REQUEST
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_MALFORMED_REQUEST,
                        InfrastructureConstants.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_MISSING_PARAMETER + ex.getParameterName(),
                        InfrastructureConstants.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_MISSING_FILE,
                        InfrastructureConstants.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(InfrastructureConstants.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_FILE_TOO_LARGE,
                        InfrastructureConstants.PAYLOAD_TOO_LARGE
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(InfrastructureConstants.CONFLICT)
                .body(new ErrorResponse(
                        InfrastructureConstants.MSG_DATA_CONFLICT,
                        InfrastructureConstants.CONFLICT
                ));
    }
}
