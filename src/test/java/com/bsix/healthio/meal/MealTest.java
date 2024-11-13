package com.bsix.healthio.meal;

import static com.bsix.healthio.meal.MealController.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.bsix.healthio.TestConfig;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class MealTest {

  public static final List<Meal> DEFAULT_MEAL_LIST =
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
  static final Sort DEFAULT_MEAL_DATE_SORT = Sort.by(Sort.Order.desc("date"));
  static final Sort DEFAULT_MEAL_TOTAL_CALORIES_SORT = Sort.by(Sort.Order.desc("totalCalories"));
  static final Pageable DEFAULT_MEAL_PAGEABLE =
      PageRequest.of(0, 10, DEFAULT_MEAL_DATE_SORT.and(DEFAULT_MEAL_TOTAL_CALORIES_SORT));
  static final Page DEFAULT_MEAL_PAGE =
      new PageImpl<>(DEFAULT_MEAL_LIST, DEFAULT_MEAL_PAGEABLE, DEFAULT_MEAL_LIST.size());

  static final MealPageRequest DEFAULT_MEAL_PAGE_REQUEST =
      new MealPageRequest(
          "1",
          DEFAULT_DATE_START,
          Instant.now(),
          DEFAULT_CONSUMED_MIN,
          DEFAULT_CONSUMED_MAX,
          DEFAULT_MEAL_PAGEABLE);

  static final Meal DEFAULT_MEAL = DEFAULT_MEAL_LIST.get(0);

  static final EntityModel<Meal> DEFAULT_MEAL_ENTITY =
      EntityModel.of(DEFAULT_MEAL, Link.of(ROOT_URI + "/" + DEFAULT_MEAL.getId()));

  static final MealMutateBody DEFAULT_MEAL_MUTATE_BODY =
      new MealMutateBody(
          Instant.now().minusSeconds(86400),
          List.of(new Meal.Food("Smoothie", 350), new Meal.Food("Pizza", 600)));
  @Autowired private TestRestTemplate http;
  @Autowired private MealRepository mealRepository;
  private List<Meal> meals;

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

  @BeforeEach
  void beforeEach() {
    meals = mealRepository.saveAll(DEFAULT_MEAL_LIST);
  }

  @Test
  void getMeals() {
    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).build().toUri();

    var req =
        RequestEntity.get(uri)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .build();

    var ref = new ParameterizedTypeReference<PagedModel<EntityModel<Meal>>>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(res.getBody().hasLink(IanaLinkRelations.SELF));
    assertThat(res.getBody().getMetadata().getNumber()).isNotNegative();
    assertThat(res.getBody().getMetadata().getSize()).isPositive();
    assertThat(res.getBody().getMetadata().getTotalElements()).isNotNegative();
    assertThat(res.getBody().getMetadata().getTotalPages()).isNotNegative();
    assertThat(res.getBody().getContent().size()).isPositive();

    System.out.println(res.getBody().getContent());
  }

  @Test
  void getMeals_WithBadParams_ShouldReturnProblemDetail() {
    var params =
        new LinkedMultiValueMap<String, String>(
            Map.of("dateStart", List.of(Instant.now().plusSeconds(3600).toString())));

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).queryParams(params).build().toUri();

    var req =
        RequestEntity.get(uri)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .build();

    var ref = new ParameterizedTypeReference<ProblemDetail>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Test
  void postMeal_WithNoDate_ShouldAddCurrentDate() {
    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).build().toUri();

    var mut = new MealMutateBody(null, List.of(new Meal.Food("Apple", 100)));

    var req =
        RequestEntity.post(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .body(mut);

    var ref = new ParameterizedTypeReference<EntityModel<Meal>>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().containsKey(HttpHeaders.LOCATION));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(res.getBody().getContent().getDate()).isNotNull();
  }

  @Test
  void postMeal_WithBadDate_ShouldReturnProblemDetail() {
    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).build().toUri();

    var mut =
        new MealMutateBody(Instant.now().plusSeconds(3600), List.of(new Meal.Food("Apple", 100)));

    var req =
        RequestEntity.post(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .body(mut);

    var ref = new ParameterizedTypeReference<EntityModel<Meal>>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().containsKey(HttpHeaders.LOCATION));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Test
  void putMeal() {
    var id = meals.get(0).getId();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var apple = new Meal.Food("Apple", 100);

    var mut = new MealMutateBody(Instant.now().minusSeconds(3600), List.of(apple));

    var req =
        RequestEntity.put(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .body(mut);

    var res = http.exchange(req, Void.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
  }

  @Test
  void deleteMeal() {
    var id = meals.get(0).getId();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var req =
        RequestEntity.delete(uri)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer tokenValue")
            .build();

    var res = http.exchange(req, Void.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));

    assertThat(mealRepository.findById(id)).isEmpty();
  }
}
