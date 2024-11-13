package com.bsix.healthio.workout;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

  public static final String ROOT_URI = "/api/v1/workouts";
  static final Instant DEFAULT_DATE_START = Instant.EPOCH;
  static final Instant DEFAULT_DATE_END = Instant.now();
  static final Integer DEFAULT_BURNED_MIN = 1;
  static final Integer DEFAULT_BURNED_MAX = 9999;
  static final Integer DEFAULT_DURATION_MIN = 1;
  static final Integer DEFAULT_DURATION_MAX = 1440;

  private final WorkoutService workoutService;

  private final PagedResourcesAssembler<Workout> workoutPageAssembler;

  private final WorkoutAssembler workoutAssembler;

  @GetMapping
  ResponseEntity<PagedModel<EntityModel<Workout>>> getWorkoutPage(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam Optional<Instant> dateStart,
      @RequestParam Optional<Instant> dateEnd,
      @RequestParam Optional<Integer> burnedMin,
      @RequestParam Optional<Integer> burnedMax,
      @RequestParam Optional<Integer> durationMin,
      @RequestParam Optional<Integer> durationMax,
      @PageableDefault Pageable pageable) {

    WorkoutPageRequest request =
        new WorkoutPageRequest(
            jwt.getSubject(),
            dateStart.orElse(DEFAULT_DATE_START),
            dateEnd.orElse(DEFAULT_DATE_END),
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
}
