package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.UserFollow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserFollowMapper extends BaseMapper<UserFollow> {

    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_id = #{userId}")
    Long selectFollowingCount(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM user_follows WHERE following_id = #{userId}")
    Long selectFollowerCount(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) > 0 FROM user_follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    boolean isFollowing(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);
}
