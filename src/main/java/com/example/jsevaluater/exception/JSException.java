package com.example.jsevaluater.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JSException extends RuntimeException {
    public JSException(String message) {
        super(message);
    }
}
