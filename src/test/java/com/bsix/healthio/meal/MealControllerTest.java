package com.bsix.healthio.meal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsix.healthio.TestSecurityConfig;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(MealController.class)
@Import({TestSecurityConfig.class, MealAssembler.class})
public class MealControllerTest {

  @Autowired MockMvc http;

  @Autowired ObjectMapper objectMapper;

  @MockBean MealService mealService;

  @Test
  void contextLoads() {
    assert http != null;
  }

  @Test
  void getMealPage() throws Exception {
    when(mealService.getMeals(any()))
        .thenReturn(
            new PageImpl<>(
                List.of(
                    new Meal(
                        UUID.randomUUID(),
                        "user",
                        Instant.now(),
                        1000,
                        List.of(new Meal.Food("apple", 100), new Meal.Food("steak", 900))))));
    http.perform(MockMvcRequestBuilders.get("/api/v1/meals").with(oauth2Login()))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.GET.name()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$._links.self.href").isString())
        .andExpect(jsonPath("$.page.number").isNumber())
        .andExpect(jsonPath("$.page.size").isNumber())
        .andExpect(jsonPath("$.page.totalPages").isNumber())
        .andExpect(jsonPath("$.page.totalElements").isNumber())
        .andExpect(jsonPath("$._embedded.mealList").isArray());
  }

  @Test
  void postMeal() throws Exception {
    when(mealService.postMeal(any()))
        .thenReturn(
            new Meal(
                UUID.randomUUID(),
                "user",
                Instant.now(),
                500,
                List.of(new Meal.Food("Potatoes", 500))));
    http.perform(
            MockMvcRequestBuilders.post("/api/v1/meals")
                .with(oauth2Login())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MealMutateBody(
                            Instant.now(), List.of(new Meal.Food("Potatoes", 500))))))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.POST.name()))
        .andExpect(jsonPath("$._links.self.href").isString())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.owner").doesNotHaveJsonPath());
  }

  @Test
  void putMeal() throws Exception {
    doNothing().when(mealService).putMeal(any(), any());
    http.perform(
            MockMvcRequestBuilders.put("/api/v1/meals/" + UUID.randomUUID())
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MealMutateBody(
                            Instant.now(),
                            List.of(new Meal.Food("Yogurt", 200), new Meal.Food("Almonds", 250))))))
        .andExpect(status().isNoContent())
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.PUT.name()));
  }

  @Test
  void deleteMeal() throws Exception {
    doNothing().when(mealService).deleteMeal(any(), any());
    http.perform(
            MockMvcRequestBuilders.delete("/api/v1/meals/" + UUID.randomUUID()).with(oauth2Login()))
        .andExpect(status().isNoContent())
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.DELETE.name()));
  }
}
