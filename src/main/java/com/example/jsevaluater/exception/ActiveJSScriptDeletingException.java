package com.example.jsevaluater.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ActiveJSScriptDeletingException extends RuntimeException {
    public ActiveJSScriptDeletingException(String message) {
        super(message);
    }
}
