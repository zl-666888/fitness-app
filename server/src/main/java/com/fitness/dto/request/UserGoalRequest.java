package com.fitness.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class UserGoalRequest {
    private String goalType;
    private BigDecimal targetWeight;
    private Integer weeklyFrequency;
    private LocalTime reminderTime;
}
