package com.bsix.healthio.meal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record MealMutateBody(
    @PastOrPresent(message = "You cannot log future meals!") Instant date,
    @Valid @Size(max = 20, message = "You cannot add more than 20 foods to a meal!")
        List<Meal.Food> foods) {}
