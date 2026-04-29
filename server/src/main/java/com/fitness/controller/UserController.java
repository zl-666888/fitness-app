package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.dto.request.UserGoalRequest;
import com.fitness.dto.request.UserProfileRequest;
import com.fitness.entity.User;
import com.fitness.entity.UserGoal;
import com.fitness.interceptor.AuthInterceptor;
import com.fitness.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户端-个人信息", description = "获取/修改个人信息、健身目标")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        userService.updateProfile(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取健身目标")
    @GetMapping("/goal")
    public Result<UserGoal> getGoal() {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(userService.getGoal(userId));
    }

    @Operation(summary = "设置健身目标")
    @PutMapping("/goal")
    public Result<Void> setGoal(@Valid @RequestBody UserGoalRequest request) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        userService.setGoal(userId, request);
        return Result.success();
    }
}
