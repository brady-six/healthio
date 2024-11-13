package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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
  ResponseEntity<PagedModel<EntityModel<Meal>>> getMeals(
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

  @PostMapping
  ResponseEntity<EntityModel<Meal>> postMeal(
      @AuthenticationPrincipal Jwt jwt, @RequestBody MealMutateBody body) {
    MealMutateRequest request = new MealMutateRequest(jwt.getSubject(), body);

    Meal meal = mealService.postMeal(request);

    EntityModel<Meal> model = mealAssembler.toModel(meal);

    return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .allow(HttpMethod.POST)
        .body(model);
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> putMeal(
      @AuthenticationPrincipal Jwt jwt, @PathVariable String id, @RequestBody MealMutateBody body) {
    MealMutateRequest request = new MealMutateRequest(jwt.getSubject(), body);

    mealService.putMeal(UUID.fromString(id), request);

    return ResponseEntity.noContent().allow(HttpMethod.PUT).build();
  }
}
