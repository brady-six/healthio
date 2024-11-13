package com.bsix.healthio;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalAdvice {

  @ExceptionHandler(ErrorResponseException.class)
  ProblemDetail handleErrorResponseException(ErrorResponseException e) {
    return e.getBody();
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
    HttpStatusCode code = HttpStatusCode.valueOf(400);

    ProblemDetail pd = ProblemDetail.forStatus(code);

    pd.setTitle("Constraint Violation Detected");

    var violation = e.getConstraintViolations().stream().findAny();

    pd.setDetail(violation.isPresent() ? violation.get().getMessage() : "");

    pd.setDetail(e.getConstraintViolations().stream().findAny().get().getMessage());
    return handleErrorResponseException(new ErrorResponseException(code, pd, e));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    HttpStatusCode code = HttpStatusCode.valueOf(400);

    ProblemDetail pd = ProblemDetail.forStatus(code);

    pd.setTitle("Argument Type Mismatch");

    pd.setDetail(
        e.getParameter().getParameterName() + " could not be handled as the expected type.");

    return handleErrorResponseException(new ErrorResponseException(code, pd, e));
  }
}
