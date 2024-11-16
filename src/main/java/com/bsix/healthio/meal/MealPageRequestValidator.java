package com.bsix.healthio.meal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;

public class MealPageRequestValidator
    implements ConstraintValidator<ValidMealPageRequest, MealPageRequest> {

  private static final String DATE_RANGE_CONSTRAINT_MSG =
      "Illegal range for dateStart and dateEnd.";

  private static final String CALORIE_RANGE_CONSTRAINT_MSG =
      "Illegal range for calorieMin and calorieMax.";

  private static boolean isValidDateRange(Instant dateStart, Instant dateEnd) {
    return dateStart.isBefore(dateEnd);
  }

  private static boolean isValidCalorieRange(Integer calorieMin, Integer calorieMax) {
    return calorieMin < calorieMax;
  }

  private static void setDateRangeConstraint(ConstraintValidatorContext context) {
    context
        .buildConstraintViolationWithTemplate(DATE_RANGE_CONSTRAINT_MSG)
        .addConstraintViolation();
  }

  private static void setCalorieConstraint(ConstraintValidatorContext context) {
    context
        .buildConstraintViolationWithTemplate(CALORIE_RANGE_CONSTRAINT_MSG)
        .addConstraintViolation();
  }

  @Override
  public boolean isValid(MealPageRequest request, ConstraintValidatorContext context) {
    boolean isValid = true;

    if (!isValidDateRange(request.dateStart(), request.dateEnd())) {
      setDateRangeConstraint(context);
      isValid = false;
    }

    if (!isValidCalorieRange(request.calorieMin(), request.calorieMax())) {
      setCalorieConstraint(context);
      isValid = false;
    }

    return isValid;
  }
}
