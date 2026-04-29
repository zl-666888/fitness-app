package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.dto.request.CheckinRequest;
import com.fitness.dto.response.CheckinStatsResponse;
import com.fitness.entity.Checkin;
import com.fitness.interceptor.AuthInterceptor;
import com.fitness.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "用户端-打卡", description = "每日打卡、打卡日历、打卡统计")
@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @Operation(summary = "每日打卡", description = "提交今日打卡记录，同一天不可重复打卡")
    @PostMapping
    public Result<Checkin> createCheckin(@Valid @RequestBody CheckinRequest request) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(checkinService.createCheckin(userId, request));
    }

    @Operation(summary = "获取今日打卡", description = "查询当前用户今日是否已打卡")
    @GetMapping("/today")
    public Result<Checkin> getTodayCheckin() {
        Integer userId = AuthInterceptor.getCurrentUserId();
        Checkin checkin = checkinService.getTodayCheckin(userId);
        return Result.success(checkin);
    }

    @Operation(summary = "获取打卡日历", description = "按月查询打卡日期列表")
    @GetMapping("/calendar")
    public Result<List<LocalDate>> getCalendar(@RequestParam Integer year, @RequestParam Integer month) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(checkinService.getCalendar(userId, year, month));
    }

    @Operation(summary = "获取打卡统计", description = "累计天数、连续天数、本月天数、总时长、总卡路里")
    @GetMapping("/stats")
    public Result<CheckinStatsResponse> getStats() {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(checkinService.getStats(userId));
    }

    @Operation(summary = "删除打卡记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCheckin(@PathVariable Integer id) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        checkinService.deleteCheckin(userId, id);
        return Result.success();
    }
}
