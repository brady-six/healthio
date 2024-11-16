package com.bsix.healthio.workout;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkoutPageRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkoutPageRequest {
  String message() default "One or more invalid ranges detected.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
