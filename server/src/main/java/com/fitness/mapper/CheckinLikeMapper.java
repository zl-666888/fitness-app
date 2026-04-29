package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.CheckinLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CheckinLikeMapper extends BaseMapper<CheckinLike> {

    @Select("SELECT COUNT(*) FROM checkin_likes WHERE checkin_id = #{checkinId}")
    Long selectLikeCount(@Param("checkinId") Integer checkinId);

    @Select("SELECT COUNT(*) > 0 FROM checkin_likes WHERE checkin_id = #{checkinId} AND user_id = #{userId}")
    boolean existsByCheckinIdAndUserId(@Param("checkinId") Integer checkinId, @Param("userId") Integer userId);
}
