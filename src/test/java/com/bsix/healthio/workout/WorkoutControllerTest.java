package com.bsix.healthio.workout;

import static com.bsix.healthio.MainTest.*;
import static com.bsix.healthio.workout.WorkoutController.*;
import static com.bsix.healthio.workout.WorkoutTest.DEFAULT_WORKOUT_ENTITY;
import static com.bsix.healthio.workout.WorkoutTest.DEFAULT_WORKOUT_PAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(WorkoutController.class)
public class WorkoutControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private WorkoutAssembler workoutAssembler;

  @MockBean private WorkoutService workoutService;

  @Test
  void getWorkouts_WithParams_ShouldUseParams() throws Exception {

    Instant dateStart = Instant.now().minusSeconds(2592000);
    Instant dateEnd = Instant.now();

    Integer burnedMin = 100;
    Integer burnedMax = 500;

    Integer durationMin = 15;
    Integer durationMax = 60;

    MultiValueMap<String, String> params =
        new LinkedMultiValueMap<>(
            Map.of(
                "dateStart", List.of(dateStart.toString()),
                "dateEnd", List.of(dateEnd.toString()),
                "burnedMin", List.of(Integer.toString(burnedMin)),
                "burnedMax", List.of(Integer.toString(burnedMax)),
                "durationMin", List.of(Integer.toString(durationMin)),
                "durationMax", List.of(Integer.toString(durationMax))));

    when(workoutService.getWorkoutPage(any())).thenReturn(DEFAULT_WORKOUT_PAGE);

    when(workoutAssembler.toModel(any())).thenReturn(DEFAULT_WORKOUT_ENTITY);

    mockMvc
        .perform(
            get("/api/v1/workouts")
                .with(jwt().jwt(DEFAULT_JWT))
                .accept(MediaType.APPLICATION_JSON)
                .params(params))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.GET.name()));

    verify(workoutService)
        .getWorkoutPage(
            new WorkoutPageRequest(
                DEFAULT_JWT.getSubject(),
                dateStart,
                dateEnd,
                burnedMin,
                burnedMax,
                durationMin,
                durationMax,
                DEFAULT_PAGEABLE));
  }

  @Test
  void getWorkouts_WithNoParams_ShouldUseDefaults() throws Exception {

    when(workoutService.getWorkoutPage(any())).thenReturn(DEFAULT_WORKOUT_PAGE);

    when(workoutAssembler.toModel(any())).thenReturn(DEFAULT_WORKOUT_ENTITY);

    mockMvc
        .perform(
            get("/api/v1/workouts").with(jwt().jwt(DEFAULT_JWT)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.GET.name()));

    verify(workoutService)
        .getWorkoutPage(
            new WorkoutPageRequest(
                DEFAULT_JWT.getSubject(),
                DEFAULT_DATE_START,
                DEFAULT_DATE_END,
                DEFAULT_BURNED_MIN,
                DEFAULT_BURNED_MAX,
                DEFAULT_DURATION_MIN,
                DEFAULT_DURATION_MAX,
                DEFAULT_PAGEABLE));
  }

  @Test
  void getWorkouts_WithBadRequest_ShouldReturnProblemDetail() throws Exception {

    when(workoutService.getWorkoutPage(any())).thenThrow(DEFAULT_BAD_REQUEST);

    Instant badStartDate = Instant.now().plusSeconds(86400);
    Instant badEndDate = badStartDate.minusSeconds(2592000);

    Integer badBurnedMin = -25;
    Integer badBurnedMax = 999_999;

    Integer okDurationMin = 10;
    Integer badDurationMax = okDurationMin - 5;

    mockMvc
        .perform(get(ROOT_URI).with(jwt().jwt(DEFAULT_JWT)).accept(MediaType.APPLICATION_JSON))
        .andExpect(matchProblemDetail());
  }
}
