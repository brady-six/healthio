package com.bsix.healthio.workout;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;

public class WorkoutPageRequestValidator
    implements ConstraintValidator<ValidWorkoutPageRequest, WorkoutPageRequest> {

  private static final String BURNED_RANGE_VIOLATION_MSG =
      "Illegal range for burnedMin and burnedMax.";

  private static final String DATE_RANGE_VIOLATION_MSG = "Illegal range for dateStart and dateEnd.";

  private static final String DURATION_RANGE_VIOLATION_MSG =
      "Illegal range for durationMin and durationMax";

  private static boolean isValidBurnedRange(Integer burnedMin, Integer burnedMax) {
    return burnedMin < burnedMax;
  }

  private static void setBurnedRangeViolation(ConstraintValidatorContext context) {
    context
        .buildConstraintViolationWithTemplate(BURNED_RANGE_VIOLATION_MSG)
        .addConstraintViolation();
  }

  private static boolean isValidDurationRange(Integer durationMin, Integer durationMax) {
    return durationMin < durationMax;
  }

  private static void setDurationRangeViolation(ConstraintValidatorContext context) {
    context
        .buildConstraintViolationWithTemplate(DURATION_RANGE_VIOLATION_MSG)
        .addConstraintViolation();
  }

  private static boolean isValidDateRange(Instant dateStart, Instant dateEnd) {
    return dateStart.isBefore(dateEnd);
  }

  private static void setDateRangeViolation(ConstraintValidatorContext context) {
    context.buildConstraintViolationWithTemplate(DATE_RANGE_VIOLATION_MSG).addConstraintViolation();
  }

  @Override
  public boolean isValid(WorkoutPageRequest request, ConstraintValidatorContext context) {
    boolean isValid = true;

    if (!isValidDateRange(request.dateStart(), request.dateEnd())) {
      setDateRangeViolation(context);
      isValid = false;
    }

    if (!isValidBurnedRange(request.burnedMin(), request.burnedMax())) {
      setBurnedRangeViolation(context);
      isValid = false;
    }

    if (!isValidDurationRange(request.durationMin(), request.durationMax())) {
      setDurationRangeViolation(context);
      isValid = false;
    }

    return isValid;
  }
}
