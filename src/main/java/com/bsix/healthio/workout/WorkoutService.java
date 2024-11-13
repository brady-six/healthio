package com.bsix.healthio.workout;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;

  Page<Workout> getWorkoutPage(@Valid WorkoutPageRequest request) {
    return workoutRepository
        .findAllByOwnerAndDateBetweenAndCaloriesBurnedBetweenAndDurationMinutesBetween(
            request.owner(),
            request.dateStart(),
            request.dateEnd(),
            request.burnedMin(),
            request.burnedMax(),
            request.durationMin(),
            request.durationMax());
  }
}
