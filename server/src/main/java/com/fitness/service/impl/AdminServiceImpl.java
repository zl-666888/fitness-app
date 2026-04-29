package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.AdminRequest;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.response.DashboardResponse;
import com.fitness.dto.response.LoginResponse;
import com.fitness.entity.Admin;
import com.fitness.entity.OperationLog;
import com.fitness.mapper.AdminMapper;
import com.fitness.mapper.CheckinMapper;
import com.fitness.mapper.FoodMapper;
import com.fitness.mapper.OperationLogMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.mapper.VideoMapper;
import com.fitness.service.AdminService;
import com.fitness.service.CheckinService;
import com.fitness.service.FoodService;
import com.fitness.service.UserService;
import com.fitness.service.VideoService;
import com.fitness.utils.JwtUtil;
import com.fitness.utils.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final OperationLogMapper operationLogMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CheckinService checkinService;
    private final VideoService videoService;
    private final FoodService foodService;

    public AdminServiceImpl(AdminMapper adminMapper, OperationLogMapper operationLogMapper,
                            PasswordUtil passwordUtil, JwtUtil jwtUtil,
                            UserService userService, CheckinService checkinService,
                            VideoService videoService, FoodService foodService) {
        this.adminMapper = adminMapper;
        this.operationLogMapper = operationLogMapper;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.checkinService = checkinService;
        this.videoService = videoService;
        this.foodService = foodService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, request.getUsername()));
        if (admin == null) {
            throw BizException.badRequest("管理员账号不存在");
        }
        if (admin.getStatus() == 0) {
            throw BizException.forbidden("管理员账号已被禁用");
        }
        if (!passwordUtil.matches(request.getPassword(), admin.getPassword())) {
            throw BizException.badRequest("密码错误");
        }

        String accessToken = jwtUtil.generateAccessToken(admin.getId(), admin.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(admin.getId(), admin.getRole());

        admin.setPassword(null);
        return new LoginResponse(accessToken, refreshToken, admin);
    }

    @Override
    public DashboardResponse getDashboard() {
        DashboardResponse resp = new DashboardResponse();
        resp.setTotalUsers(userService.countTotalUsers());
        resp.setTodayCheckins(checkinService.countTodayCheckins());
        resp.setTotalVideos(videoService.countTotalVideos());
        resp.setTotalFoods(foodService.countTotalFoods());
        resp.setTodayNewUsers(userService.countTodayNewUsers());
        resp.setWeeklyCheckinTrend(getWeeklyCheckinTrend());
        resp.setUserGrowthTrend(getUserGrowthTrend());
        return resp;
    }

    @Override
    public IPage<Admin> getAdminList(Integer page, Integer size) {
        return adminMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Admin>().orderByDesc(Admin::getCreatedAt));
    }

    @Override
    @Transactional
    public Admin createAdmin(AdminRequest request, Integer operatorId) {
        if (adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, request.getUsername())) > 0) {
            throw BizException.conflict("管理员账号已存在");
        }
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordUtil.encode(request.getPassword()));
        admin.setRole(request.getRole() != null ? request.getRole() : "admin");
        admin.setRealName(request.getRealName());
        adminMapper.insert(admin);
        return admin;
    }

    @Override
    @Transactional
    public void deleteAdmin(Integer adminId, Integer operatorId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw BizException.notFound("管理员不存在");
        }
        if ("super".equals(admin.getRole())) {
            throw BizException.forbidden("不能删除超级管理员");
        }
        adminMapper.deleteById(adminId);
    }

    @Override
    @Transactional
    public void updateAdmin(Integer adminId, AdminRequest request, Integer operatorId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw BizException.notFound("管理员不存在");
        }
        admin.setRealName(request.getRealName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(passwordUtil.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            admin.setRole(request.getRole());
        }
        adminMapper.updateById(admin);
    }

    @Override
    public IPage<OperationLog> getLogs(Integer page, Integer size) {
        return operationLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreatedAt));
    }

    @Override
    public void addLog(Integer adminId, String action, String targetType, Integer targetId, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(ip);
        operationLogMapper.insert(log);
    }

    private List<Map<String, Object>> getWeeklyCheckinTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Map<String, Object> point = new HashMap<>();
            point.put("date", date.toString());
            point.put("count", 0);
            trend.add(point);
        }
        return trend;
    }

    private List<Map<String, Object>> getUserGrowthTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Map<String, Object> point = new HashMap<>();
            point.put("date", date.toString());
            point.put("count", 0);
            trend.add(point);
        }
        return trend;
    }
}
