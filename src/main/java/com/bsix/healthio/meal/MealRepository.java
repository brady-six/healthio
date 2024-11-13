package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MealRepository extends JpaRepository<Meal, UUID> {
  @Query(
      """
    SELECT m FROM Meal m
    WHERE m.owner = :owner
      AND m.date BETWEEN :dateStart AND :dateEnd
      AND m.totalCalories BETWEEN :calorieMin AND :calorieMax
    """)
  Page<Meal> findMealPage(
      @Param("owner") String owner,
      @Param("dateStart") Instant dateStart,
      @Param("dateEnd") Instant dateEnd,
      @Param("calorieMin") Integer calorieMin,
      @Param("calorieMax") Integer calorieMax,
      Pageable pageable);

  Page<Meal> findAllByOwner(String owner, Pageable pageable);

  Optional<Meal> findByIdAndOwner(UUID id, String owner);
}
