package com.example.jsevaluater.exception.handler;

import com.example.jsevaluater.exception.ActiveJSScriptDeletingException;
import com.example.jsevaluater.exception.InternalServerException;
import com.example.jsevaluater.exception.JSException;
import com.example.jsevaluater.exception.JSScriptNotFoundException;
import com.example.jsevaluater.payload.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(value = JSException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleJSException(JSException e) {
        log.error(e.getMessage(), e);
        return Response.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleInternalServerException(InternalServerException e) {
        log.error(e.getMessage(), e);
        return Response.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = JSScriptNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleJSScriptNotFoundException(JSScriptNotFoundException e) {
        log.error(e.getMessage(), e);
        return Response.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = ActiveJSScriptDeletingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleActiveJSScriptDeletingException(ActiveJSScriptDeletingException e) {
        log.error(e.getMessage(), e);
        return Response.builder()
                .message(e.getMessage())
                .build();
    }

}
