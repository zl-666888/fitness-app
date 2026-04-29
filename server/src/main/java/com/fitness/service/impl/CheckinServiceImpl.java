package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.CheckinRequest;
import com.fitness.dto.response.CheckinStatsResponse;
import com.fitness.dto.response.FeedResponse;
import com.fitness.entity.Checkin;
import com.fitness.entity.User;
import com.fitness.mapper.CheckinLikeMapper;
import com.fitness.mapper.CheckinMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.CheckinService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckinServiceImpl implements CheckinService {

    private final CheckinMapper checkinMapper;
    private final CheckinLikeMapper checkinLikeMapper;
    private final UserMapper userMapper;

    public CheckinServiceImpl(CheckinMapper checkinMapper, CheckinLikeMapper checkinLikeMapper,
                              UserMapper userMapper) {
        this.checkinMapper = checkinMapper;
        this.checkinLikeMapper = checkinLikeMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Checkin createCheckin(Integer userId, CheckinRequest request) {
        Checkin existing = checkinMapper.selectOne(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getUserId, userId)
                .eq(Checkin::getCheckinDate, request.getCheckinDate()));
        if (existing != null) {
            throw BizException.conflict("今日已打卡，不能重复打卡");
        }

        Checkin checkin = new Checkin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(request.getCheckinDate());
        checkin.setCheckinTime(request.getCheckinTime());
        checkin.setDuration(request.getDuration());
        checkin.setType(request.getType());
        checkin.setIntensity(request.getIntensity());
        checkin.setContent(request.getContent());
        checkin.setCaloriesBurned(request.getCaloriesBurned());
        checkinMapper.insert(checkin);
        return checkin;
    }

    @Override
    public Checkin getTodayCheckin(Integer userId) {
        return checkinMapper.selectOne(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getUserId, userId)
                .eq(Checkin::getCheckinDate, LocalDate.now()));
    }

    @Override
    public List<LocalDate> getCalendar(Integer userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return checkinMapper.selectCheckinDatesByMonth(userId, startDate, endDate);
    }

    @Override
    public CheckinStatsResponse getStats(Integer userId) {
        CheckinStatsResponse stats = new CheckinStatsResponse();

        long totalDays = checkinMapper.selectTotalCheckinDays(userId);
        stats.setTotalDays(totalDays);

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        long currentMonthDays = checkinMapper.selectCheckinDaysSince(userId, monthStart);
        stats.setCurrentMonthDays(currentMonthDays);

        long consecutiveDays = calculateConsecutiveDays(userId);
        stats.setConsecutiveDays(consecutiveDays);

        List<Checkin> allCheckins = checkinMapper.selectList(
                new LambdaQueryWrapper<Checkin>().eq(Checkin::getUserId, userId));
        long totalDuration = allCheckins.stream()
                .filter(c -> c.getDuration() != null)
                .mapToLong(Checkin::getDuration).sum();
        long totalCalories = allCheckins.stream()
                .filter(c -> c.getCaloriesBurned() != null)
                .mapToLong(Checkin::getCaloriesBurned).sum();
        stats.setTotalDuration(totalDuration);
        stats.setTotalCalories(totalCalories);

        List<String> dates = allCheckins.stream()
                .map(c -> c.getCheckinDate().toString())
                .sorted()
                .collect(Collectors.toList());
        stats.setCheckinDates(dates);

        return stats;
    }

    @Override
    public void deleteCheckin(Integer userId, Integer checkinId) {
        Checkin checkin = checkinMapper.selectById(checkinId);
        if (checkin == null) {
            throw BizException.notFound("打卡记录不存在");
        }
        if (!checkin.getUserId().equals(userId)) {
            throw BizException.forbidden("只能删除自己的打卡记录");
        }
        checkinMapper.deleteById(checkinId);
    }

    @Override
    public long countTodayCheckins() {
        return checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getCheckinDate, LocalDate.now()));
    }

    @Override
    public List<Checkin> getRecentCheckins() {
        return checkinMapper.selectList(
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getCheckinDate, LocalDate.now())
                        .orderByDesc(Checkin::getCreatedAt));
    }

    @Override
    public IPage<FeedResponse> getFeed(Integer currentUserId, Integer page, Integer size) {
        IPage<Checkin> checkinPage = checkinMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Checkin>()
                        .orderByDesc(Checkin::getCheckinDate, Checkin::getCreatedAt));

        IPage<FeedResponse> result = new Page<>(checkinPage.getCurrent(), checkinPage.getSize(), checkinPage.getTotal());
        List<FeedResponse> records = checkinPage.getRecords().stream().map(c -> {
            FeedResponse feed = new FeedResponse();
            feed.setCheckinId(c.getId());
            feed.setUserId(c.getUserId());
            feed.setCheckinDate(c.getCheckinDate());
            feed.setCheckinTime(c.getCheckinTime());
            feed.setDuration(c.getDuration());
            feed.setType(c.getType());
            feed.setIntensity(c.getIntensity());
            feed.setContent(c.getContent());
            feed.setCaloriesBurned(c.getCaloriesBurned());
            feed.setCreatedAt(c.getCreatedAt());

            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                feed.setNickname(user.getNickname());
                feed.setAvatar(user.getAvatar());
            }

            feed.setLikeCount(checkinLikeMapper.selectLikeCount(c.getId()));
            feed.setCommentCount(0);
            if (currentUserId != null) {
                feed.setLiked(checkinLikeMapper.existsByCheckinIdAndUserId(c.getId(), currentUserId));
            }
            return feed;
        }).collect(Collectors.toList());

        result.setRecords(records);
        return result;
    }

    private long calculateConsecutiveDays(Integer userId) {
        List<Checkin> checkins = checkinMapper.selectList(
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getUserId, userId)
                        .orderByDesc(Checkin::getCheckinDate));
        if (checkins.isEmpty()) return 0;

        List<LocalDate> dates = checkins.stream()
                .map(Checkin::getCheckinDate)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        long count = 1;
        for (int i = 0; i < dates.size() - 1; i++) {
            if (ChronoUnit.DAYS.between(dates.get(i + 1), dates.get(i)) == 1) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
