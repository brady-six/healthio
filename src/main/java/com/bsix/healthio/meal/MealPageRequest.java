package com.bsix.healthio.meal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import org.springframework.data.domain.Pageable;

@ValidMealPageRequest
public record MealPageRequest(
    String owner,
    @PastOrPresent(message = "You cannot search for future dates!") Instant dateStart,
    @PastOrPresent(message = "You cannot search for future dates!") Instant dateEnd,
    @Min(value = 1, message = "You cannot search for meals with less than 1 calorie consumed!")
        @Max(
            value = 100_000,
            message = "You cannot search for meals with more than 100,000 calories consumed!")
        Integer calorieMin,
    @Min(value = 1, message = "You cannot search for meals with less than 1 calorie consumed!")
        @Max(
            value = 100_000,
            message = "You cannot search for meals with more than 100,000 calories consumed!")
        Integer calorieMax,
    Pageable pageable) {}
