package com.bsix.healthio.workout;

import static com.bsix.healthio.workout.WorkoutController.ROOT_URI;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bsix.healthio.TestConfig;
import java.time.Instant;
import java.util.List;
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
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class WorkoutTest {

  public static final List<Workout> DEFAULT_WORKOUT_LIST =
      List.of(
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(10 * 86400), 300, 30),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(6 * 86400), 350, 35),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(4 * 86400), 621, 60),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(2 * 86400), 250, 25),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(86400), 500, 50));
  public static final Workout DEFAULT_WORKOUT = DEFAULT_WORKOUT_LIST.get(0);
  public static final WorkoutMutateBody DEFAULT_WORKOUT_MUTATE_BODY =
      new WorkoutMutateBody(
          DEFAULT_WORKOUT.getDate(),
          DEFAULT_WORKOUT.getCaloriesBurned(),
          DEFAULT_WORKOUT.getDurationMinutes());
  public static final EntityModel<Workout> DEFAULT_WORKOUT_ENTITY =
      EntityModel.of(
          DEFAULT_WORKOUT,
          Link.of("/api/v1/workouts/" + DEFAULT_WORKOUT.getId(), IanaLinkRelations.SELF));
  static final Sort DEFAULT_WORKOUT_DATE_SORT = Sort.by(Sort.Order.desc("date"));
  static final Sort DEFAULT_WORKOUT_CALORIES_BURNED_SORT =
      Sort.by(Sort.Order.desc("caloriesBurned"));
  static final Sort DEFAULT_WORKOUT_DURATION_MINUTES_SORT =
      Sort.by(Sort.Order.desc("durationMinutes"));
  static final Pageable DEFAULT_WORKOUT_PAGEABLE =
      PageRequest.of(
          0,
          10,
          DEFAULT_WORKOUT_DATE_SORT
              .and(DEFAULT_WORKOUT_CALORIES_BURNED_SORT)
              .and(DEFAULT_WORKOUT_DURATION_MINUTES_SORT));
  public static final Page<Workout> DEFAULT_WORKOUT_PAGE =
      new PageImpl<>(DEFAULT_WORKOUT_LIST, DEFAULT_WORKOUT_PAGEABLE, DEFAULT_WORKOUT_LIST.size());

  @Autowired private TestRestTemplate http;

  @Autowired private WorkoutRepository workoutRepository;

  private List<Workout> workouts;

  @BeforeEach
  void beforeEach() {
    workouts = workoutRepository.saveAll(DEFAULT_WORKOUT_LIST);
  }

  @Test
  void getWorkouts() {
    var req =
        RequestEntity.get(ROOT_URI)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .build();

    var ref = new ParameterizedTypeReference<PagedModel<EntityModel<Workout>>>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getBody().getMetadata().getSize()).isNotNegative();
    assertThat(res.getBody().getMetadata().getNumber()).isNotNegative();
    assertThat(res.getBody().getMetadata().getTotalPages()).isNotNegative();
    assertThat(res.getBody().getMetadata().getTotalElements()).isNotNegative();
    assertThat(res.getBody().getContent().size()).isPositive();
  }

  @Test
  void getWorkouts_WithBadParams_ShouldReturnProblemDetail() {
    var uri =
        UriComponentsBuilder.fromUriString(ROOT_URI)
            .queryParam("dateStart", Instant.now().plusSeconds(3600).toString())
            .build()
            .toUri();

    var req =
        RequestEntity.get(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .build();

    var ref = new ParameterizedTypeReference<ProblemDetail>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Test
  void postWorkout() {
    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).build().toUri();

    var mut = new WorkoutMutateBody(null, 350, 32);

    var req =
        RequestEntity.post(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .body(mut);

    var ref = new ParameterizedTypeReference<EntityModel<Workout>>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().containsKey(HttpHeaders.LOCATION));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(res.getBody().hasLink(IanaLinkRelations.SELF));
    assertThat(res.getBody().getContent().getDate()).isNotNull();
  }

  @Test
  void postWorkout_WithBadRequest_ShouldReturnProblemDetail() {
    var uri = UriComponentsBuilder.fromUriString(ROOT_URI).build().toUri();

    var mut = new WorkoutMutateBody(Instant.EPOCH.minusSeconds(3600), -235, -32);

    var req =
        RequestEntity.post(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .body(mut);

    var ref = new ParameterizedTypeReference<ProblemDetail>() {};

    var res = http.exchange(req, ref);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Test
  void putWorkout() {
    var id = workouts.get(0).getId();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var mut = new WorkoutMutateBody(null, 320, 30);

    var req =
        RequestEntity.put(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .body(mut);

    var res = http.exchange(req, Void.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(workoutRepository.findById(id).get().getDate()).isNotNull();
  }

  @Test
  void putWorkout_WithBadRequest_ShouldReturnProblemDetail() {
    var id = workouts.get(0).getId();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var mut = new WorkoutMutateBody(Instant.EPOCH.minusSeconds(12 * 2592000), -320, -30);

    var req =
        RequestEntity.put(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .accept(MediaType.APPLICATION_JSON)
            .body(mut);

    var res = http.exchange(req, ProblemDetail.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }

  @Test
  void deleteWorkout() {
    var id = workouts.get(0).getId();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var req = RequestEntity.delete(uri).header(HttpHeaders.AUTHORIZATION, "Bearer token").build();

    var res = http.exchange(req, Void.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
  }

  @Test
  void deleteWorkout_WithUnknownId_ShouldReturnProblemDetail() {
    var id = UUID.randomUUID();

    var uri = UriComponentsBuilder.fromUriString(ROOT_URI + "/" + id).build().toUri();

    var req = RequestEntity.delete(uri).header(HttpHeaders.AUTHORIZATION, "Bearer token").build();

    var res = http.exchange(req, ProblemDetail.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res.getHeaders().containsKey(HttpHeaders.ALLOW));
    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
  }
}
