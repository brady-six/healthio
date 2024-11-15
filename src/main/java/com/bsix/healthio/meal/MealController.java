package com.bsix.healthio.meal;

import java.time.Instant;
import java.util.*;
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

@RestController
@RequestMapping("/api/v1/meals")
public class MealController {

  static final String ROOT_URI = "/api/v1/meals";
  static final Instant DEFAULT_DATE_START = Instant.EPOCH;
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

  @PostMapping
  ResponseEntity<EntityModel<Meal>> postMeal(
      @AuthenticationPrincipal OAuth2User user, @RequestBody MealMutateBody body) {
    MealMutateRequest request = new MealMutateRequest(user.getAttribute("sub"), body);

    Meal meal = mealService.postMeal(request);

    EntityModel<Meal> model = mealAssembler.toModel(meal);

    return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .allow(HttpMethod.POST)
        .body(model);
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> putMeal(
      @AuthenticationPrincipal OAuth2User user,
      @PathVariable String id,
      @RequestBody MealMutateBody body) {
    MealMutateRequest request = new MealMutateRequest(user.getAttribute("sub"), body);

    mealService.putMeal(UUID.fromString(id), request);

    return ResponseEntity.noContent().allow(HttpMethod.PUT).build();
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteMeal(
      @AuthenticationPrincipal OAuth2User user, @PathVariable String id) {
    mealService.deleteMeal(UUID.fromString(id), user.getAttribute("sub"));

    return ResponseEntity.noContent().allow(HttpMethod.DELETE).build();
  }

  @GetMapping
  ResponseEntity<PagedModel<EntityModel<Meal>>> getMeals(
      @AuthenticationPrincipal OAuth2User user,
      @RequestParam Optional<Instant> dateStart,
      @RequestParam Optional<Instant> dateEnd,
      @RequestParam Optional<Integer> consumedMin,
      @RequestParam Optional<Integer> consumedMax,
      @PageableDefault
          @SortDefault.SortDefaults({
            @SortDefault(sort = "date", direction = Sort.Direction.DESC),
            @SortDefault(sort = "totalCalories", direction = Sort.Direction.DESC)
          })
          Pageable pageable) {
    MealPageRequest request =
        new MealPageRequest(
            user.getAttribute("sub"),
            dateStart.orElse(DEFAULT_DATE_START),
            dateEnd.orElse(Instant.now()),
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
