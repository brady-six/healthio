package com.bsix.healthio.meal;

import static com.bsix.healthio.meal.MealController.ROOT_URI;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MealAssembler implements RepresentationModelAssembler<Meal, EntityModel<Meal>> {

  @Override
  public EntityModel<Meal> toModel(Meal meal) {
    return EntityModel.of(meal, Link.of(ROOT_URI + "/" + meal.getId()));
  }
}
