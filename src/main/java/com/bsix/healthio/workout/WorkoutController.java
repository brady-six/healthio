package com.bsix.healthio.workout;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

  public static final String ROOT_URI = "/api/v1/workouts";
  static final Instant DEFAULT_DATE_START = Instant.EPOCH;
  static final Integer DEFAULT_BURNED_MIN = 1;
  static final Integer DEFAULT_BURNED_MAX = 9999;
  static final Integer DEFAULT_DURATION_MIN = 1;
  static final Integer DEFAULT_DURATION_MAX = 1440;

  private final WorkoutService workoutService;

  private final PagedResourcesAssembler<Workout> workoutPageAssembler;

  private final WorkoutAssembler workoutAssembler;

  @GetMapping
  ResponseEntity<PagedModel<EntityModel<Workout>>> getWorkoutPage(
      @AuthenticationPrincipal OAuth2User user,
      @RequestParam Optional<Instant> dateStart,
      @RequestParam Optional<Instant> dateEnd,
      @RequestParam Optional<Integer> burnedMin,
      @RequestParam Optional<Integer> burnedMax,
      @RequestParam Optional<Integer> durationMin,
      @RequestParam Optional<Integer> durationMax,
      @PageableDefault
          @SortDefault.SortDefaults({
            @SortDefault(sort = "date", direction = Sort.Direction.DESC),
            @SortDefault(sort = "caloriesBurned", direction = Sort.Direction.DESC),
            @SortDefault(sort = "durationMinutes", direction = Sort.Direction.DESC)
          })
          Pageable pageable) {

    WorkoutPageRequest request =
        new WorkoutPageRequest(
            user.getAttribute("sub"),
            dateStart.orElse(DEFAULT_DATE_START),
            dateEnd.orElse(Instant.now()),
            burnedMin.orElse(DEFAULT_BURNED_MIN),
            burnedMax.orElse(DEFAULT_BURNED_MAX),
            durationMin.orElse(DEFAULT_DURATION_MIN),
            durationMax.orElse(DEFAULT_DURATION_MAX),
            pageable);

    Page<Workout> page = workoutService.getWorkoutPage(request);

    PagedModel<EntityModel<Workout>> model =
        workoutPageAssembler.toModel(
            page, workoutAssembler, Link.of(ROOT_URI, IanaLinkRelations.SELF));

    return ResponseEntity.ok()
        .allow(HttpMethod.GET)
        .contentType(MediaType.APPLICATION_JSON)
        .body(model);
  }

  @PostMapping
  ResponseEntity<EntityModel<Workout>> postWorkout(
      @AuthenticationPrincipal OAuth2User user, @RequestBody WorkoutMutateBody body) {
    WorkoutMutateRequest request = new WorkoutMutateRequest(user.getAttribute("sub"), body);

    Workout workout = workoutService.postWorkout(request);

    EntityModel<Workout> model = workoutAssembler.toModel(workout);

    return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .contentType(MediaType.APPLICATION_JSON)
        .allow(HttpMethod.POST)
        .body(model);
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> putWorkout(
      @AuthenticationPrincipal OAuth2User user,
      @PathVariable String id,
      @RequestBody WorkoutMutateBody body) {
    WorkoutMutateRequest request = new WorkoutMutateRequest(user.getAttribute("sub"), body);

    workoutService.putWorkout(UUID.fromString(id), request);

    return ResponseEntity.noContent().allow(HttpMethod.PUT).build();
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteWorkout(
      @AuthenticationPrincipal OAuth2User user, @PathVariable String id) {
    workoutService.deleteWorkout(UUID.fromString(id), user.getAttribute("sub"));

    return ResponseEntity.noContent().allow(HttpMethod.DELETE).build();
  }
}
