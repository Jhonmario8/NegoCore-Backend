package com.negocore.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String ErrorResponse;
    private int status;
    private Map<String, String> errors;

    public ErrorResponse(String message, int status) {
        this.ErrorResponse = message;
        this.status = status;
    }

}

