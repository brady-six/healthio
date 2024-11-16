package com.bsix.healthio.workout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Workout {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @JsonIgnore private String owner;

  private Instant date;

  private Integer caloriesBurned;

  private Integer durationMinutes;

  public String toAiPromptString() {
    return "On "
        + this.getDate()
        + ", I burned "
        + this.getCaloriesBurned()
        + " calories and exercised for "
        + this.getDurationMinutes()
        + " minutes.";
  }
}
