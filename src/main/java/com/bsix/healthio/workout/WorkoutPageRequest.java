package com.bsix.healthio.workout;

import jakarta.validation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.lang.annotation.*;
import java.time.Instant;
import org.springframework.data.domain.Pageable;

@ValidWorkoutPageRequest
public record WorkoutPageRequest(
    String owner,
    @PastOrPresent(message = "You cannot search for future dates!") Instant dateStart,
    @PastOrPresent(message = "You cannot search for future dates!") Instant dateEnd,
    @Min(value = 1, message = "You cannot search for workouts with fewer than 1 calorie burned!")
        @Max(
            value = 9999,
            message = "You cannot search for workouts with more than 9999 calories burned!")
        Integer burnedMin,
    @Min(value = 1, message = "You cannot search for workouts with fewer than 1 calorie burned!")
        @Max(
            value = 9999,
            message = "You cannot search for workouts with more than 9999 calories burned!")
        Integer burnedMax,
    @Min(value = 1, message = "You cannot search for workouts shorter than 1 minute!")
        @Max(value = 1440, message = "You cannot search for workouts longer than 24 hours!")
        Integer durationMin,
    @Min(value = 1, message = "You cannot search for workouts shorter than 1 minute!")
        @Max(value = 1440, message = "You cannot search for workouts longer than 24 hours!")
        Integer durationMax,
    Pageable pageable) {}
