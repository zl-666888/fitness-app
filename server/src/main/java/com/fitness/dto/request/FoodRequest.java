package com.fitness.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FoodRequest {
    @NotBlank(message = "食物名称不能为空")
    private String name;

    private String category;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private BigDecimal fiber;
    private String unit;
    private String imageUrl;
}
