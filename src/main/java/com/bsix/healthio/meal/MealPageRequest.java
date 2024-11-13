package com.bsix.healthio.meal;

import java.time.Instant;
import org.springframework.data.domain.Pageable;

public record MealPageRequest(
    String owner,
    Instant dateStart,
    Instant dateEnd,
    Integer calorieMin,
    Integer calorieMax,
    Pageable pageable) {}
