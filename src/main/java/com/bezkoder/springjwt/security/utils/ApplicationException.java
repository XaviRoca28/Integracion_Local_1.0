package com.bezkoder.springjwt.security.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApplicationException extends RuntimeException{

    private ApiErrorResponse smrError;
    private HttpStatus httpStatus;
    private LocalDateTime moment;

    public ApplicationException(ApiErrorResponse smrError, HttpStatus httpStatus) {
        this.smrError = smrError;
        this.httpStatus = httpStatus;
        this.moment = LocalDateTime.now();
    }


}
