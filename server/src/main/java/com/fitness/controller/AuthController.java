package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.request.RefreshTokenRequest;
import com.fitness.dto.request.RegisterRequest;
import com.fitness.dto.response.LoginResponse;
import com.fitness.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户端-认证", description = "用户注册、登录、退出等")
@RestController
@RequestMapping("/api/user")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册", description = "注册新用户，返回token和用户信息")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @Operation(summary = "用户登录", description = "用户名密码登录，返回token和用户信息")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "退出登录", description = "客户端清除token即可")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary = "刷新令牌", description = "使用refreshToken获取新的accessToken和refreshToken")
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.success(userService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "重置密码", description = "预留接口，暂未实现")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword() {
        return Result.success();
    }
}
