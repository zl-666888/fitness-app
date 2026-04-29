package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.dto.request.CommentRequest;
import com.fitness.dto.response.FeedResponse;
import com.fitness.entity.CheckinComment;
import com.fitness.entity.UserFollow;
import com.fitness.interceptor.AuthInterceptor;
import com.fitness.service.CheckinService;
import com.fitness.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "用户端-社交", description = "打卡广场、点赞、评论、关注")
@RestController
public class SocialController {

    private final SocialService socialService;
    private final CheckinService checkinService;

    public SocialController(SocialService socialService, CheckinService checkinService) {
        this.socialService = socialService;
        this.checkinService = checkinService;
    }

    @Operation(summary = "打卡广场", description = "查看所有用户的打卡动态，按时间倒序")
    @GetMapping("/api/feed")
    public Result<PageResult<FeedResponse>> getFeed(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        IPage<FeedResponse> result = checkinService.getFeed(userId, page, size);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "点赞/取消点赞", description = "切换点赞状态")
    @PostMapping("/api/checkin/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Integer id) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        boolean liked = socialService.toggleLike(userId, id);
        return Result.success(Map.of("liked", liked));
    }

    @Operation(summary = "评论打卡", description = "对打卡记录发表评论")
    @PostMapping("/api/checkin/{id}/comment")
    public Result<CheckinComment> addComment(@PathVariable Integer id,
                                              @Valid @RequestBody CommentRequest request) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(socialService.addComment(userId, id, request));
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/api/checkin/{id}/comments")
    public Result<List<CheckinComment>> getComments(@PathVariable Integer id) {
        return Result.success(socialService.getComments(id));
    }

    @Operation(summary = "关注用户")
    @PostMapping("/api/user/follow/{userId}")
    public Result<Void> follow(@PathVariable Integer userId) {
        Integer currentUserId = AuthInterceptor.getCurrentUserId();
        socialService.follow(currentUserId, userId);
        return Result.success();
    }

    @Operation(summary = "取消关注")
    @PostMapping("/api/user/unfollow/{userId}")
    public Result<Void> unfollow(@PathVariable Integer userId) {
        Integer currentUserId = AuthInterceptor.getCurrentUserId();
        socialService.unfollow(currentUserId, userId);
        return Result.success();
    }

    @Operation(summary = "获取关注列表")
    @GetMapping("/api/user/following")
    public Result<PageResult<UserFollow>> getFollowingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(PageResult.of(socialService.getFollowingList(userId, page, size)));
    }

    @Operation(summary = "获取粉丝列表")
    @GetMapping("/api/user/follower")
    public Result<PageResult<UserFollow>> getFollowerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        return Result.success(PageResult.of(socialService.getFollowerList(userId, page, size)));
    }
}
