package com.bsix.healthio.meal;

import static com.bsix.healthio.MainTest.*;
import static com.bsix.healthio.meal.MealController.ROOT_URI;
import static com.bsix.healthio.meal.MealTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(MealController.class)
public class MealControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private MealService mealService;

  @MockBean private MealAssembler mealAssembler;

  @Test
  void contextLoads() {
    assert mockMvc != null;
  }

  @Test
  void getMeals_WithNoParams_ShouldUseDefaults() throws Exception {
    when(mealService.getMeals(any())).thenReturn(DEFAULT_MEAL_PAGE);

    when(mealAssembler.toModel(any())).thenReturn(DEFAULT_MEAL_ENTITY);

    mockMvc.perform(get(ROOT_URI).with(jwt().jwt(DEFAULT_JWT)));

    verify(mealService, times(1)).getMeals(DEFAULT_MEAL_PAGE_REQUEST);
  }

  @Test
  void getMeals_WithBadRequest_ShouldReturnProblemDetail() throws Exception {
    when(mealService.getMeals(any())).thenThrow(DEFAULT_BAD_REQUEST);

    MultiValueMap<String, String> params =
        new LinkedMultiValueMap<>(
            Map.of("dateStart", List.of(Instant.now().plusSeconds(3600).toString())));

    mockMvc
        .perform(
            get(ROOT_URI)
                .with(jwt().jwt(DEFAULT_JWT))
                .params(params)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(matchProblemDetail());
  }

  @Test
  void postMeal_ShouldReturnCreated() throws Exception {
    when(mealService.postMeal(any())).thenReturn(DEFAULT_MEAL);

    when(mealAssembler.toModel(any())).thenReturn(DEFAULT_MEAL_ENTITY);

    mockMvc
        .perform(
            post(ROOT_URI)
                .with(jwt().jwt(DEFAULT_JWT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(DEFAULT_MEAL_MUTATE_BODY)))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.ALLOW, HttpMethod.POST.name()))
        .andExpect(header().string(HttpHeaders.LOCATION, ROOT_URI + "/" + DEFAULT_MEAL.getId()));
  }

  @Test
  void postMeal_ShouldReturnMealEntity() throws Exception {
    when(mealService.postMeal(any())).thenReturn(DEFAULT_MEAL);

    when(mealAssembler.toModel(any())).thenReturn(DEFAULT_MEAL_ENTITY);

    mockMvc
        .perform(
            post(ROOT_URI)
                .with(jwt().jwt(DEFAULT_JWT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(DEFAULT_MEAL_MUTATE_BODY)))
        .andExpect(matchMealEntity());
  }
}
