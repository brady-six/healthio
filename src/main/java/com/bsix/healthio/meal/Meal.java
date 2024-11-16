package com.bsix.healthio.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

  public String toAiPromptString() {
    StringBuilder sb = new StringBuilder();
    sb.append("On " + this.getDate() + ", I ate ");
    if (this.getFoods().size() == 1)
      return sb.toString() + this.getFoods().get(0).toAiPromptString() + ".";

    for (int i = 0; i < this.getFoods().size() - 1; i++) {
      sb.append(this.getFoods().get(i).toAiPromptString()).append(", ");
    }

    sb.append("and " + this.getFoods().get(this.getFoods().size() - 1).toAiPromptString())
        .append(".");

    return sb.toString();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Embeddable
  public static class Food {

    @Size(max = 50, message = "You cannot name a food with more than 50 characters!")
    private String name;

    @Min(value = 1, message = "You cannot add a food with less than 1 calorie!")
    @Max(value = 10_000, message = "You cannot add a food with more than 10,000 calories!")
    private Integer calories;

    public String toAiPromptString() {
      return this.getCalories() + " calories of " + this.getName();
    }
  }
}
