package com.bsix.healthio.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @JsonIgnore private String owner;

  private Instant date;

  private Integer totalCalories;

  @ElementCollection private List<Food> foods;

  static Integer calculateTotalCalories(List<Food> foods) {
    return foods.stream().mapToInt(Food::getCalories).sum();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Embeddable
  public static class Food {

    private String name;
    private Integer calories;
  }
}
