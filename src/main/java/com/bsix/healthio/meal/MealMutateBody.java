package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.List;

public record MealMutateBody(Instant date, List<Meal.Food> foods) {}
