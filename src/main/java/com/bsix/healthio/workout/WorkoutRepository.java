package com.bsix.healthio.workout;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
  Page<Workout> findAllByOwnerAndDateBetweenAndCaloriesBurnedBetweenAndDurationMinutesBetween(
      String owner,
      Instant dateStart,
      Instant dateEnd,
      Integer burnedMin,
      Integer burnedMax,
      Integer durationMin,
      Integer durationMax);
}
