package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.dto.response.DashboardResponse;
import com.fitness.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-仪表盘", description = "数据概览统计")
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminService adminService;

    public AdminDashboardController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "数据概览", description = "用户总数、今日打卡数、视频数、食物数、趋势数据")
    @GetMapping("/dashboard")
    public Result<DashboardResponse> getDashboard() {
        return Result.success(adminService.getDashboard());
    }
}
