package com.bsix.healthio.meal;

import jakarta.validation.Valid;

public record MealMutateRequest(String owner, @Valid MealMutateBody body) {}
