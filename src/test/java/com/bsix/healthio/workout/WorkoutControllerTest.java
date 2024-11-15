package com.bsix.healthio.workout;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsix.healthio.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(WorkoutController.class)
@Import({WorkoutAssembler.class, SecurityConfig.class})
public class WorkoutControllerTest {

  @Autowired MockMvc http;

  @Autowired WorkoutAssembler workoutAssembler;

  @Autowired ObjectMapper objectMapper;

  @MockBean WorkoutService workoutService;

  @Test
  void contextLoads() {
    assert http != null;
  }

  @Test
  void getWorkoutPage() throws Exception {
    when(workoutService.getWorkoutPage(any()))
        .thenReturn(
            new PageImpl<>(
                List.of(new Workout(UUID.randomUUID(), "user", Instant.now(), 500, 30))));
    var dateEnd = Instant.now();
    http.perform(
            MockMvcRequestBuilders.get("/api/v1/workouts")
                .with(oauth2Login())
                .param("dateEnd", dateEnd.toString()))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.GET.name()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$._links.self.href").isString())
        .andExpect(jsonPath("$.page.size").isNumber())
        .andExpect(jsonPath("$.page.number").isNumber())
        .andExpect(jsonPath("$.page.totalPages").isNumber())
        .andExpect(jsonPath("$.page.totalElements").isNumber())
        .andExpect(jsonPath("$._embedded.workoutList").isArray());

    // Verify that default query params were used
    var req =
        new WorkoutPageRequest(
            "user",
            Instant.EPOCH,
            dateEnd,
            1,
            9999,
            1,
            1440,
            PageRequest.of(
                0, 10, Sort.by(Sort.Direction.DESC, "date", "caloriesBurned", "durationMinutes")));

    verify(workoutService).getWorkoutPage(req);
  }

  @Test
  void postWorkout() throws Exception {
    when(workoutService.postWorkout(any()))
        .thenReturn(new Workout(UUID.randomUUID(), "user", Instant.now(), 530, 32));
    http.perform(
            MockMvcRequestBuilders.post("/api/v1/workouts")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new WorkoutMutateBody(Instant.now(), 530, 32))))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.POST.name()))
        .andExpect(jsonPath("$._links.self.href").isString())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.owner").doesNotHaveJsonPath());
  }

  @Test
  void putWorkout() throws Exception {
    doNothing().when(workoutService).putWorkout(any(), any());
    http.perform(
            MockMvcRequestBuilders.put("/api/v1/workouts/" + UUID.randomUUID())
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new WorkoutMutateBody(Instant.now(), 321, 32))))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.PUT.name()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteWorkout() throws Exception {
    doNothing().when(workoutService).deleteWorkout(any(), any());
    http.perform(
            MockMvcRequestBuilders.delete("/api/v1/workouts/" + UUID.randomUUID())
                .with(oauth2Login()))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.DELETE.name()))
        .andExpect(status().isNoContent());
  }
}
