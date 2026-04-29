package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.response.LoginResponse;
import com.fitness.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-认证", description = "管理员登录/退出")
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "管理员登录", description = "使用管理员账号密码登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(adminService.login(request));
    }

    @Operation(summary = "管理员退出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
