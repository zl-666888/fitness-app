package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.dto.request.UserProfileRequest;
import com.fitness.entity.User;
import com.fitness.interceptor.AdminAuthInterceptor;
import com.fitness.service.AdminService;
import com.fitness.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-用户管理", description = "管理端用户CRUD、禁用启用")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final AdminService adminService;

    public AdminUserController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @Operation(summary = "用户列表", description = "分页查询，支持按用户名/手机号搜索")
    @GetMapping
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = userService.getAdminUserList(page, size, keyword);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<User> getUserDetail(@PathVariable Integer id) {
        User user = userService.getAdminUserDetail(id);
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "编辑用户", description = "修改用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Integer id,
                                    @RequestBody UserProfileRequest request,
                                    HttpServletRequest httpRequest) {
        userService.updateAdminUser(id, request);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "update_user", "user", id,
                "编辑用户", httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "删除用户", description = "删除用户及其关联数据（打卡、评论等）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id, HttpServletRequest httpRequest) {
        userService.deleteUser(id);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "delete_user", "user", id,
                "删除用户", httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "禁用/启用用户", description = "切换用户账号状态")
    @PutMapping("/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Integer id, HttpServletRequest httpRequest) {
        userService.toggleUserStatus(id);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "toggle_user_status", "user", id,
                "切换用户状态", httpRequest.getRemoteAddr());
        return Result.success();
    }
}
