package com.bsix.healthio.meal;

import java.util.UUID;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class MealNotFoundException extends ErrorResponseException {
  public MealNotFoundException(UUID id) {
    super(
        HttpStatusCode.valueOf(404),
        ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(404), "Could not find meal with id " + id),
        new IllegalArgumentException());
  }
}
