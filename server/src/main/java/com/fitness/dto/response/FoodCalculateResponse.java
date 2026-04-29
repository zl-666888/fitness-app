package com.fitness.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FoodCalculateResponse {
    private String foodName;
    private BigDecimal amount;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
}
