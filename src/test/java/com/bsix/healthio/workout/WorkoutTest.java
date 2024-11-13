package com.bsix.healthio.workout;

import static com.bsix.healthio.MainTest.DEFAULT_PAGEABLE;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;

public class WorkoutTest {

  public static final List<Workout> DEFAULT_WORKOUT_LIST =
      List.of(
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(10 * 86400), 300, 30),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(6 * 86400), 350, 35),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(4 * 86400), 621, 60),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(2 * 86400), 250, 25),
          new Workout(UUID.randomUUID(), "1", Instant.now().minusSeconds(86400), 500, 50));

  public static final Workout DEFAULT_WORKOUT = DEFAULT_WORKOUT_LIST.get(0);

  public static final EntityModel<Workout> DEFAULT_WORKOUT_ENTITY =
      EntityModel.of(
          DEFAULT_WORKOUT,
          Link.of("/api/v1/workouts/" + DEFAULT_WORKOUT.getId(), IanaLinkRelations.SELF));

  public static final Page<Workout> DEFAULT_WORKOUT_PAGE =
      new PageImpl<>(DEFAULT_WORKOUT_LIST, DEFAULT_PAGEABLE, DEFAULT_WORKOUT_LIST.size());
}
