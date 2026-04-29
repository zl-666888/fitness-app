package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.CommentRequest;
import com.fitness.entity.CheckinComment;
import com.fitness.entity.UserFollow;

import java.util.List;

public interface SocialService {
    boolean toggleLike(Integer userId, Integer checkinId);
    CheckinComment addComment(Integer userId, Integer checkinId, CommentRequest request);
    List<CheckinComment> getComments(Integer checkinId);
    void follow(Integer followerId, Integer followingId);
    void unfollow(Integer followerId, Integer followingId);
    boolean isFollowing(Integer followerId, Integer followingId);
    long getFollowingCount(Integer userId);
    long getFollowerCount(Integer userId);
    IPage<UserFollow> getFollowingList(Integer userId, Integer page, Integer size);
    IPage<UserFollow> getFollowerList(Integer userId, Integer page, Integer size);
}
