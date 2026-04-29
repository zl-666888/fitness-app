package com.fitness.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FoodCalculateRequest {
    @NotNull(message = "食物ID不能为空")
    private Integer foodId;

    @NotNull(message = "份量不能为空")
    private BigDecimal amount;
}
