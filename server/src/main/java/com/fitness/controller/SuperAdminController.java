package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.AdminRequest;
import com.fitness.entity.Admin;
import com.fitness.entity.OperationLog;
import com.fitness.interceptor.AdminAuthInterceptor;
import com.fitness.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "超级管理员", description = "管理员管理、操作日志（仅super角色可访问）")
@RestController
@RequestMapping("/api/admin")
public class SuperAdminController {

    private final AdminService adminService;

    public SuperAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "管理员列表")
    @GetMapping("/admins")
    public Result<PageResult<Admin>> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        checkSuperAdmin();
        IPage<Admin> result = adminService.getAdminList(page, size);
        result.getRecords().forEach(a -> a.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "添加管理员")
    @PostMapping("/admins")
    public Result<Admin> createAdmin(@Valid @RequestBody AdminRequest request,
                                      HttpServletRequest httpRequest) {
        checkSuperAdmin();
        Integer operatorId = AdminAuthInterceptor.getCurrentAdminId();
        Admin admin = adminService.createAdmin(request, operatorId);
        admin.setPassword(null);
        adminService.addLog(operatorId, "add_admin", "admin", admin.getId(),
                "添加管理员", httpRequest.getRemoteAddr());
        return Result.success(admin);
    }

    @Operation(summary = "编辑管理员", description = "修改管理员信息/角色/密码")
    @PutMapping("/admins/{id}")
    public Result<Void> updateAdmin(@PathVariable Integer id,
                                     @RequestBody AdminRequest request,
                                     HttpServletRequest httpRequest) {
        checkSuperAdmin();
        Integer operatorId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.updateAdmin(id, request, operatorId);
        adminService.addLog(operatorId, "update_admin", "admin", id,
                "编辑管理员", httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "删除管理员", description = "不可删除super角色")
    @DeleteMapping("/admins/{id}")
    public Result<Void> deleteAdmin(@PathVariable Integer id, HttpServletRequest httpRequest) {
        checkSuperAdmin();
        Integer operatorId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.deleteAdmin(id, operatorId);
        adminService.addLog(operatorId, "delete_admin", "admin", id,
                "删除管理员", httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "操作日志", description = "查看管理员操作历史")
    @GetMapping("/logs")
    public Result<PageResult<OperationLog>> getLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        checkSuperAdmin();
        return Result.success(PageResult.of(adminService.getLogs(page, size)));
    }

    private void checkSuperAdmin() {
        String role = AdminAuthInterceptor.getCurrentAdminRole();
        if (!"super".equals(role)) {
            throw BizException.forbidden("仅超级管理员可执行此操作");
        }
    }
}
