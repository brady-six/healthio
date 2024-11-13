package com.bsix.healthio.workout;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
  @Query(
      """
    SELECT w FROM Workout w
    WHERE w.owner = :owner
      AND w.date BETWEEN :dateStart AND :dateEnd
      AND w.caloriesBurned BETWEEN :burnedMin AND :burnedMax
      AND w.durationMinutes BETWEEN :durationMin AND :durationMax
    """)
  Page<Workout> findWorkoutPage(
      @Param("owner") String owner,
      @Param("dateStart") Instant dateStart,
      @Param("dateEnd") Instant dateEnd,
      @Param("burnedMin") Integer burnedMin,
      @Param("burnedMax") Integer burnedMax,
      @Param("durationMin") Integer durationMin,
      @Param("durationMax") Integer durationMax,
      Pageable pageable);

  Page<Workout> findAllByOwner(String owner, Pageable pageable);

  Optional<Workout> findByIdAndOwner(UUID id, String owner);
}
