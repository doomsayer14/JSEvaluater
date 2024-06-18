package com.example.jsevaluater.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JSScriptNotFoundException extends RuntimeException {
    public JSScriptNotFoundException(String message) {
        super(message);
    }
}
