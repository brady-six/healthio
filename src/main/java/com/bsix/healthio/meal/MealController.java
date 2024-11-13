package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meals")
public class MealController {

  static final String ROOT_URI = "/api/v1/meals";
  static final Instant DEFAULT_DATE_START = Instant.EPOCH;
  static final Instant DEFAULT_DATE_END = Instant.now();
  static final Integer DEFAULT_CONSUMED_MIN = 1;
  static final Integer DEFAULT_CONSUMED_MAX = 100_000;

  private final MealService mealService;

  private final PagedResourcesAssembler<Meal> mealPageAssembler;

  private final MealAssembler mealAssembler;

  public MealController(
      MealService mealService,
      PagedResourcesAssembler<Meal> mealPageAssembler,
      MealAssembler mealAssembler) {
    this.mealService = mealService;
    this.mealPageAssembler = mealPageAssembler;
    this.mealAssembler = mealAssembler;
  }

  @GetMapping
  ResponseEntity<PagedModel<EntityModel<Meal>>> getWorkouts(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam Optional<Instant> dateStart,
      @RequestParam Optional<Instant> dateEnd,
      @RequestParam Optional<Integer> consumedMin,
      @RequestParam Optional<Integer> consumedMax,
      @PageableDefault Pageable pageable) {
    MealPageRequest request =
        new MealPageRequest(
            jwt.getSubject(),
            dateStart.orElse(DEFAULT_DATE_START),
            dateEnd.orElse(DEFAULT_DATE_END),
            consumedMin.orElse(DEFAULT_CONSUMED_MIN),
            consumedMax.orElse(DEFAULT_CONSUMED_MAX),
            pageable);

    Page<Meal> page = mealService.getMeals(request);

    PagedModel<EntityModel<Meal>> model =
        mealPageAssembler.toModel(page, mealAssembler, Link.of(ROOT_URI, IanaLinkRelations.SELF));

    return ResponseEntity.ok()
        .allow(HttpMethod.GET)
        .contentType(MediaType.APPLICATION_JSON)
        .body(model);
  }
}
