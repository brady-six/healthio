package com.bsix.healthio.workout;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class WorkoutAssembler
    implements RepresentationModelAssembler<Workout, EntityModel<Workout>> {

  @Override
  public EntityModel<Workout> toModel(Workout workout) {
    return EntityModel.of(workout, Link.of(WorkoutController.ROOT_URI, IanaLinkRelations.SELF));
  }
}
