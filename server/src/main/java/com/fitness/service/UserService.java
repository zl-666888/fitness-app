package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.request.RegisterRequest;
import com.fitness.dto.request.UserGoalRequest;
import com.fitness.dto.request.UserProfileRequest;
import com.fitness.dto.response.LoginResponse;
import com.fitness.entity.User;
import com.fitness.entity.UserGoal;

public interface UserService {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(String refreshToken);
    User getProfile(Integer userId);
    void updateProfile(Integer userId, UserProfileRequest request);
    UserGoal getGoal(Integer userId);
    void setGoal(Integer userId, UserGoalRequest request);
    IPage<User> getAdminUserList(Integer page, Integer size, String keyword);
    User getAdminUserDetail(Integer userId);
    void updateAdminUser(Integer userId, UserProfileRequest request);
    void deleteUser(Integer userId);
    void toggleUserStatus(Integer userId);
    long countTotalUsers();
    long countTodayNewUsers();
}
