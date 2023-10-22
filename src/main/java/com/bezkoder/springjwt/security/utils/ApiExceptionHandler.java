package com.bezkoder.springjwt.security.utils;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handle(ApplicationException exception){

        Map<String, Object> error = new HashMap<>();
        error.put("code",exception.getSmrError().getErrorCode());
        error.put("message",exception.getSmrError().getMessage());
        error.put("moment",exception.getMoment());

        return new ResponseEntity<>(error,exception.getHttpStatus());
    }
}
