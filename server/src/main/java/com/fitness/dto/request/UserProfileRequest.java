package com.fitness.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserProfileRequest {
    private String nickname;
    private BigDecimal height;
    private BigDecimal weight;
    private Integer gender;
    private LocalDate birthDate;
    private String phone;
}
