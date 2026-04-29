package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.dto.request.VideoRequest;
import com.fitness.entity.Video;
import com.fitness.interceptor.AdminAuthInterceptor;
import com.fitness.service.AdminService;
import com.fitness.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端-视频管理", description = "视频CRUD操作")
@RestController
@RequestMapping("/api/admin/videos")
public class AdminVideoController {

    private final VideoService videoService;
    private final AdminService adminService;

    public AdminVideoController(VideoService videoService, AdminService adminService) {
        this.videoService = videoService;
        this.adminService = adminService;
    }

    @Operation(summary = "视频列表（管理端）", description = "查看所有视频，包括下架的")
    @GetMapping
    public Result<PageResult<Video>> getVideoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        IPage<Video> result = videoService.getVideoList(page, size, category, null);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "添加视频")
    @PostMapping
    public Result<Video> createVideo(@Valid @RequestBody VideoRequest request,
                                      HttpServletRequest httpRequest) {
        Video video = videoService.createVideo(request);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "add_video", "video", video.getId(),
                "添加视频", httpRequest.getRemoteAddr());
        return Result.success(video);
    }

    @Operation(summary = "编辑视频")
    @PutMapping("/{id}")
    public Result<Video> updateVideo(@PathVariable Integer id,
                                      @Valid @RequestBody VideoRequest request,
                                      HttpServletRequest httpRequest) {
        Video video = videoService.updateVideo(id, request);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "update_video", "video", id,
                "编辑视频", httpRequest.getRemoteAddr());
        return Result.success(video);
    }

    @Operation(summary = "删除视频")
    @DeleteMapping("/{id}")
    public Result<Void> deleteVideo(@PathVariable Integer id, HttpServletRequest httpRequest) {
        videoService.deleteVideo(id);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "delete_video", "video", id,
                "删除视频", httpRequest.getRemoteAddr());
        return Result.success();
    }
}
