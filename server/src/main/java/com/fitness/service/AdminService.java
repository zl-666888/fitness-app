package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.AdminRequest;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.response.DashboardResponse;
import com.fitness.dto.response.LoginResponse;
import com.fitness.entity.Admin;
import com.fitness.entity.OperationLog;

public interface AdminService {
    LoginResponse login(LoginRequest request);
    DashboardResponse getDashboard();
    IPage<Admin> getAdminList(Integer page, Integer size);
    Admin createAdmin(AdminRequest request, Integer operatorId);
    void deleteAdmin(Integer adminId, Integer operatorId);
    void updateAdmin(Integer adminId, AdminRequest request, Integer operatorId);
    IPage<OperationLog> getLogs(Integer page, Integer size);
    void addLog(Integer adminId, String action, String targetType, Integer targetId, String detail, String ip);
}
