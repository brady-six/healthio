package com.bsix.healthio.meal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MealService {

  private final MealRepository mealRepository;

  public MealService(MealRepository mealRepository) {
    this.mealRepository = mealRepository;
  }

  Page<Meal> getMeals(MealPageRequest request) {
    return mealRepository.findByOwnerAndDateBetweenAndTotalCaloriesBetween(
        request.owner(),
        request.dateStart(),
        request.dateEnd(),
        request.calorieMin(),
        request.calorieMax(),
        request.pageable());
  }
}
