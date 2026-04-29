package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.LoginRequest;
import com.fitness.dto.request.RegisterRequest;
import com.fitness.dto.request.UserGoalRequest;
import com.fitness.dto.request.UserProfileRequest;
import com.fitness.dto.response.LoginResponse;
import com.fitness.entity.User;
import com.fitness.entity.UserGoal;
import com.fitness.mapper.UserGoalMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.UserService;
import com.fitness.utils.JwtUtil;
import com.fitness.utils.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserGoalMapper userGoalMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, UserGoalMapper userGoalMapper,
                           PasswordUtil passwordUtil, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.userGoalMapper = userGoalMapper;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())) > 0) {
            throw BizException.conflict("用户名已存在");
        }
        if (request.getPhone() != null && userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone())) > 0) {
            throw BizException.conflict("手机号已注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtil.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        userMapper.insert(user);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), "user");
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), "user");

        user.setPassword(null);
        return new LoginResponse(accessToken, refreshToken, user);
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw BizException.unauthorized("刷新令牌无效或已过期");
        }
        String role = jwtUtil.getRole(refreshToken);
        if (!"user".equals(role)) {
            throw BizException.unauthorized("刷新令牌角色不正确");
        }
        Integer userId = jwtUtil.getUserId(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.unauthorized("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw BizException.forbidden("账号已被禁用");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, "user");
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, "user");

        user.setPassword(null);
        return new LoginResponse(newAccessToken, newRefreshToken, user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw BizException.badRequest("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw BizException.forbidden("账号已被禁用");
        }
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw BizException.badRequest("密码错误");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), "user");
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), "user");

        user.setPassword(null);
        return new LoginResponse(accessToken, refreshToken, user);
    }

    @Override
    public User getProfile(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateProfile(Integer userId, UserProfileRequest request) {
        User user = new User();
        user.setId(userId);
        user.setNickname(request.getNickname());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());
        user.setPhone(request.getPhone());
        userMapper.updateById(user);
    }

    @Override
    public UserGoal getGoal(Integer userId) {
        return userGoalMapper.selectOne(new LambdaQueryWrapper<UserGoal>()
                .eq(UserGoal::getUserId, userId));
    }

    @Override
    public void setGoal(Integer userId, UserGoalRequest request) {
        UserGoal goal = userGoalMapper.selectOne(new LambdaQueryWrapper<UserGoal>()
                .eq(UserGoal::getUserId, userId));
        if (goal == null) {
            goal = new UserGoal();
            goal.setUserId(userId);
        }
        goal.setGoalType(request.getGoalType());
        goal.setTargetWeight(request.getTargetWeight());
        goal.setWeeklyFrequency(request.getWeeklyFrequency());
        goal.setReminderTime(request.getReminderTime());

        if (goal.getId() == null) {
            userGoalMapper.insert(goal);
        } else {
            userGoalMapper.updateById(goal);
        }
    }

    @Override
    public IPage<User> getAdminUserList(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public User getAdminUserDetail(Integer userId) {
        return getProfile(userId);
    }

    @Override
    public void updateAdminUser(Integer userId, UserProfileRequest request) {
        updateProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        userMapper.deleteById(userId);
    }

    @Override
    public void toggleUserStatus(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.notFound("用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userMapper.updateById(user);
    }

    @Override
    public long countTotalUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public long countTodayNewUsers() {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .ge(User::getCreatedAt, LocalDate.now().atStartOfDay()));
    }
}
