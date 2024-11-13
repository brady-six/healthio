package com.bsix.healthio;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(ErrorResponseException.class)
    ProblemDetail handleErrorResponseException(ErrorResponseException e) {
        return e.getBody();
    }
}
