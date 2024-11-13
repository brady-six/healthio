package com.bsix.healthio.meal;

import static com.bsix.healthio.MainTest.DEFAULT_PAGEABLE;
import static com.bsix.healthio.meal.MealController.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.test.web.servlet.ResultMatcher;

public class MealTest {

  static final List<Meal> DEFAULT_MEAL_LIST =
      List.of(
          new Meal(
              UUID.randomUUID(),
              "1",
              Instant.now().minusSeconds(12 * 3600),
              360,
              List.of(new Meal.Food("Apple", 200), new Meal.Food("Yogurt", 160))),
          new Meal(
              UUID.randomUUID(),
              "1",
              Instant.now().minusSeconds(7 * 3600),
              700,
              List.of(new Meal.Food("Hamburger", 500), new Meal.Food("Soda", 200))),
          new Meal(
              UUID.randomUUID(),
              "2",
              Instant.now().minusSeconds(2 * 3600),
              360,
              List.of(new Meal.Food("Cereal", 200), new Meal.Food("Milk", 160))),
          new Meal(
              UUID.randomUUID(),
              "2",
              Instant.now().minusSeconds(4 * 3600),
              520,
              List.of(new Meal.Food("Sandwich", 400), new Meal.Food("Banana", 120))));

  static final Page DEFAULT_MEAL_PAGE =
      new PageImpl<>(DEFAULT_MEAL_LIST, DEFAULT_PAGEABLE, DEFAULT_MEAL_LIST.size());

  static final MealPageRequest DEFAULT_MEAL_PAGE_REQUEST =
      new MealPageRequest(
          "1",
          DEFAULT_DATE_START,
          DEFAULT_DATE_END,
          DEFAULT_CONSUMED_MIN,
          DEFAULT_CONSUMED_MAX,
          DEFAULT_PAGEABLE);

  static final Meal DEFAULT_MEAL = DEFAULT_MEAL_LIST.get(0);

  static final EntityModel<Meal> DEFAULT_MEAL_ENTITY =
      EntityModel.of(DEFAULT_MEAL, Link.of(ROOT_URI + "/" + DEFAULT_MEAL.getId()));

  static final MealMutateBody DEFAULT_MEAL_MUTATE_BODY =
      new MealMutateBody(
          Instant.now().minusSeconds(86400),
          List.of(new Meal.Food("Smoothie", 350), new Meal.Food("Pizza", 600)));

  static final ResultMatcher matchMealEntity() {
    return result -> {
      jsonPath("$._links.self.href").isString().match(result);
      jsonPath("$.id").isString().match(result);
      jsonPath("$.date").isString().match(result);
      jsonPath("$.totalCalories").isNumber().match(result);
      jsonPath("$.foods").isArray().match(result);
      jsonPath("$.owner").doesNotExist().match(result);
    };
  }
}
