package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.entity.Video;
import com.fitness.interceptor.AuthInterceptor;
import com.fitness.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "用户端-视频", description = "健身视频浏览、搜索、收藏")
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Operation(summary = "视频列表", description = "分页按分类/难度浏览视频")
    @GetMapping
    public Result<PageResult<Video>> getVideoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        IPage<Video> result = videoService.getVideoList(page, size, category, difficulty);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "搜索视频", description = "按标题/描述/教练搜索")
    @GetMapping("/search")
    public Result<PageResult<Video>> searchVideos(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Video> result = videoService.searchVideos(page, size, keyword);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "视频详情")
    @GetMapping("/{id}")
    public Result<Video> getVideoDetail(@PathVariable Integer id) {
        return Result.success(videoService.getVideoDetail(id));
    }

    @Operation(summary = "收藏/取消收藏", description = "切换收藏状态，返回当前是否已收藏")
    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Integer id) {
        Integer userId = AuthInterceptor.getCurrentUserId();
        videoService.toggleFavorite(userId, id);
        boolean favorited = videoService.isFavorited(userId, id);
        return Result.success(Map.of("favorited", favorited));
    }
}
