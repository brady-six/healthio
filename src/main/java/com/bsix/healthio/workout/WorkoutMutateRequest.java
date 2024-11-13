package com.bsix.healthio.workout;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record WorkoutMutateRequest(
    String owner,
    @PastOrPresent(message = "You cannot log future workouts!") Instant date,
    @Min(value = 1, message = "Your workout must burn at least 1 calorie!")
        @Max(value = 9999, message = "Your workout cannot burn more than 9,999 calories!")
        Integer caloriesBurned,
    @Min(value = 1, message = "Your workout must be at least 1 minute long!")
        @Max(value = 1440, message = "Your workout cannot exceed 24 hours!")
        Integer durationMinutes) {}
