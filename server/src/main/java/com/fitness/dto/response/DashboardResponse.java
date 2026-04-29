package com.fitness.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    private long totalUsers;
    private long todayCheckins;
    private long totalVideos;
    private long totalFoods;
    private long todayNewUsers;
    private List<Map<String, Object>> weeklyCheckinTrend;
    private List<Map<String, Object>> userGrowthTrend;
}
