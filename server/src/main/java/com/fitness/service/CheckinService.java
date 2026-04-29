package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.CheckinRequest;
import com.fitness.dto.response.CheckinStatsResponse;
import com.fitness.dto.response.FeedResponse;
import com.fitness.entity.Checkin;

import java.time.LocalDate;
import java.util.List;

public interface CheckinService {
    Checkin createCheckin(Integer userId, CheckinRequest request);
    Checkin getTodayCheckin(Integer userId);
    List<LocalDate> getCalendar(Integer userId, Integer year, Integer month);
    CheckinStatsResponse getStats(Integer userId);
    void deleteCheckin(Integer userId, Integer checkinId);
    long countTodayCheckins();
    List<Checkin> getRecentCheckins();
    IPage<FeedResponse> getFeed(Integer currentUserId, Integer page, Integer size);
}
