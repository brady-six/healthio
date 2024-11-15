package com.bsix.healthio.workout;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@RequiredArgsConstructor
@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;

  Page<Workout> getWorkoutPage(@Valid WorkoutPageRequest request) {
    return workoutRepository.findWorkoutPage(
        request.owner(),
        request.dateStart(),
        request.dateEnd(),
        request.burnedMin(),
        request.burnedMax(),
        request.durationMin(),
        request.durationMax(),
        request.pageable());
  }

  Workout postWorkout(@Valid WorkoutMutateRequest request) {
    Workout workout = new Workout();

    workout.setOwner(request.owner());
    workout.setDate(request.body().date() != null ? request.body().date() : Instant.now());
    workout.setCaloriesBurned(request.body().caloriesBurned());
    workout.setDurationMinutes(request.body().durationMinutes());

    return workoutRepository.save(workout);
  }

  public void putWorkout(UUID id, @Valid WorkoutMutateRequest request) {
    Workout workout = findWorkout(id, request.owner());

    workout.setDate(request.body().date() != null ? request.body().date() : Instant.now());
    workout.setCaloriesBurned(request.body().caloriesBurned());
    workout.setDurationMinutes(request.body().durationMinutes());

    workoutRepository.save(workout);
  }

  Workout findWorkout(UUID id, String owner) {
    return workoutRepository
        .findByIdAndOwner(id, owner)
        .orElseThrow(() -> new WorkoutNotFoundException(id));
  }

  public void deleteWorkout(UUID id, String owner) {
    Workout workout = findWorkout(id, owner);

    workoutRepository.deleteById(workout.getId());
  }
}
