package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.Checkin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface CheckinMapper extends BaseMapper<Checkin> {

    @Select("SELECT checkin_date FROM checkins WHERE user_id = #{userId} AND checkin_date BETWEEN #{startDate} AND #{endDate}")
    List<LocalDate> selectCheckinDatesByMonth(@Param("userId") Integer userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Select("SELECT COUNT(DISTINCT checkin_date) FROM checkins WHERE user_id = #{userId}")
    Long selectTotalCheckinDays(@Param("userId") Integer userId);

    @Select("SELECT COUNT(DISTINCT checkin_date) FROM checkins WHERE user_id = #{userId} AND checkin_date >= #{sinceDate}")
    Long selectCheckinDaysSince(@Param("userId") Integer userId, @Param("sinceDate") LocalDate sinceDate);
}
