package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.utils.VideoTranscoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文件上传", description = "通用文件上传接口（图片/视频）")
@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${server.port}")
    private int serverPort;

    private final VideoTranscoder videoTranscoder;

    public UploadController(VideoTranscoder videoTranscoder) {
        this.videoTranscoder = videoTranscoder;
    }

    @Operation(summary = "上传文件", description = "上传图片/视频，返回可访问的URL")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                               HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
        String fileName = UUID.randomUUID() + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            if (videoTranscoder.isVideoFile(originalName)) {
                videoTranscoder.ensureH264(dest);
            }

            String scheme = request.getScheme();
            String host = request.getServerName();
            String url = scheme + "://" + host + ":" + serverPort + "/uploads/" + fileName;
            return Result.success(Map.of("url", url));
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
