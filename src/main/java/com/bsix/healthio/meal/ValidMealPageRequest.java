package com.bsix.healthio.meal;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MealPageRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMealPageRequest {
  String message() default "One or more invalid ranges detected.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
