package com.fitness.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CheckinStatsResponse {
    private long totalDays;
    private long currentMonthDays;
    private long consecutiveDays;
    private long totalDuration;
    private long totalCalories;
    private List<String> checkinDates;
}
