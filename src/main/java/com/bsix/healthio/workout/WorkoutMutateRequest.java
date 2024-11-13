package com.bsix.healthio.workout;

import jakarta.validation.Valid;

public record WorkoutMutateRequest(String owner, @Valid WorkoutMutateBody body) {}
