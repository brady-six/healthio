package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, UUID> {
  Page<Meal> findByOwnerAndDateBetweenAndTotalCaloriesBetween(
      String owner,
      Instant dateStart,
      Instant dateEnd,
      Integer calorieMin,
      Integer calorieMax,
      Pageable pageable);
}
