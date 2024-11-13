package com.bsix.healthio.workout;

import java.util.UUID;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class WorkoutNotFoundException extends ErrorResponseException {
  public WorkoutNotFoundException(UUID id) {
    super(
        HttpStatusCode.valueOf(404),
        ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(404), "Could not locate workout with id " + id),
        new IllegalArgumentException());
  }
}
