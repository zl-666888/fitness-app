package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.CommentRequest;
import com.fitness.entity.Checkin;
import com.fitness.entity.CheckinComment;
import com.fitness.entity.CheckinLike;
import com.fitness.entity.UserFollow;
import com.fitness.mapper.CheckinCommentMapper;
import com.fitness.mapper.CheckinLikeMapper;
import com.fitness.mapper.CheckinMapper;
import com.fitness.mapper.UserFollowMapper;
import com.fitness.service.SocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialServiceImpl implements SocialService {

    private final CheckinLikeMapper checkinLikeMapper;
    private final CheckinCommentMapper checkinCommentMapper;
    private final CheckinMapper checkinMapper;
    private final UserFollowMapper userFollowMapper;

    public SocialServiceImpl(CheckinLikeMapper checkinLikeMapper,
                             CheckinCommentMapper checkinCommentMapper,
                             CheckinMapper checkinMapper,
                             UserFollowMapper userFollowMapper) {
        this.checkinLikeMapper = checkinLikeMapper;
        this.checkinCommentMapper = checkinCommentMapper;
        this.checkinMapper = checkinMapper;
        this.userFollowMapper = userFollowMapper;
    }

    @Override
    @Transactional
    public boolean toggleLike(Integer userId, Integer checkinId) {
        Checkin checkin = checkinMapper.selectById(checkinId);
        if (checkin == null) {
            throw BizException.notFound("打卡记录不存在");
        }
        CheckinLike like = checkinLikeMapper.selectOne(
                new LambdaQueryWrapper<CheckinLike>()
                        .eq(CheckinLike::getCheckinId, checkinId)
                        .eq(CheckinLike::getUserId, userId));
        if (like == null) {
            like = new CheckinLike();
            like.setCheckinId(checkinId);
            like.setUserId(userId);
            checkinLikeMapper.insert(like);
            return true;
        } else {
            checkinLikeMapper.deleteById(like.getId());
            return false;
        }
    }

    @Override
    public CheckinComment addComment(Integer userId, Integer checkinId, CommentRequest request) {
        Checkin checkin = checkinMapper.selectById(checkinId);
        if (checkin == null) {
            throw BizException.notFound("打卡记录不存在");
        }
        CheckinComment comment = new CheckinComment();
        comment.setCheckinId(checkinId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        checkinCommentMapper.insert(comment);
        return comment;
    }

    @Override
    public List<CheckinComment> getComments(Integer checkinId) {
        return checkinCommentMapper.selectList(
                new LambdaQueryWrapper<CheckinComment>()
                        .eq(CheckinComment::getCheckinId, checkinId)
                        .orderByDesc(CheckinComment::getCreatedAt));
    }

    @Override
    @Transactional
    public void follow(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw BizException.badRequest("不能关注自己");
        }
        if (userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, followerId)
                        .eq(UserFollow::getFollowingId, followingId)) > 0) {
            throw BizException.conflict("已关注该用户");
        }
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);
    }

    @Override
    @Transactional
    public void unfollow(Integer followerId, Integer followingId) {
        userFollowMapper.delete(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, followerId)
                        .eq(UserFollow::getFollowingId, followingId));
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followingId) {
        return userFollowMapper.isFollowing(followerId, followingId);
    }

    @Override
    public long getFollowingCount(Integer userId) {
        return userFollowMapper.selectFollowingCount(userId);
    }

    @Override
    public long getFollowerCount(Integer userId) {
        return userFollowMapper.selectFollowerCount(userId);
    }

    @Override
    public IPage<UserFollow> getFollowingList(Integer userId, Integer page, Integer size) {
        return userFollowMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, userId)
                        .orderByDesc(UserFollow::getCreatedAt));
    }

    @Override
    public IPage<UserFollow> getFollowerList(Integer userId, Integer page, Integer size) {
        return userFollowMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowingId, userId)
                        .orderByDesc(UserFollow::getCreatedAt));
    }
}
