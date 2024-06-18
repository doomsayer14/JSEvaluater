package com.example.jsevaluater.exception.handler;

import com.example.jsevaluater.exception.JSException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = JSException.class)
    public

}
