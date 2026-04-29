package com.fitness.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

@Component
public class VideoTranscoder {

    private static final Logger log = LoggerFactory.getLogger(VideoTranscoder.class);
    private static final String[] VIDEO_EXTENSIONS = {".mp4", ".mov", ".avi", ".mkv", ".flv", ".wmv", ".webm", ".m4v"};
    private static final String TARGET_CODEC = "h264";

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${ffmpeg.probe-path}")
    private String ffprobePath;

    public boolean isVideoFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase(Locale.ROOT);
        for (String ext : VIDEO_EXTENSIONS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    public String detectVideoCodec(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    ffprobePath,
                    "-v", "error",
                    "-select_streams", "v:0",
                    "-show_entries", "stream=codec_name",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    file.getAbsolutePath()
            );
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String codec = reader.readLine();
                int exitCode = process.waitFor();
                if (exitCode == 0 && codec != null) {
                    return codec.trim().toLowerCase(Locale.ROOT);
                }
            }
        } catch (Exception e) {
            log.warn("检测视频编码失败: {}", e.getMessage());
        }
        return null;
    }

    public boolean needsTranscoding(File file) {
        String codec = detectVideoCodec(file);
        if (codec == null) {
            log.warn("无法检测视频编码，跳过转码: {}", file.getName());
            return false;
        }
        boolean needsIt = !TARGET_CODEC.equals(codec);
        if (needsIt) {
            log.info("视频 {} 编码为 {}，需要转码为 H.264", file.getName(), codec);
        } else {
            log.info("视频 {} 已是 H.264 编码，无需转码", file.getName());
        }
        return needsIt;
    }

    public File transcodeToH264(File inputFile) {
        String baseName = inputFile.getName();
        int dotIdx = baseName.lastIndexOf('.');
        String nameWithoutExt = (dotIdx > 0) ? baseName.substring(0, dotIdx) : baseName;
        File tempFile = new File(inputFile.getParent(), nameWithoutExt + "_h264_tmp.mp4");

        try {
            log.info("开始转码: {} -> {}", inputFile.getName(), tempFile.getName());
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-i", inputFile.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "23",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-movflags", "+faststart",
                    "-y",
                    tempFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[ffmpeg] {}", line);
                }
            }
            int exitCode = process.waitFor();

            if (exitCode != 0 || !tempFile.exists() || tempFile.length() == 0) {
                log.error("转码失败，exitCode={}", exitCode);
                if (tempFile.exists()) tempFile.delete();
                return null;
            }

            Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("转码完成，已替换原文件: {}", inputFile.getName());
            return inputFile;

        } catch (Exception e) {
            log.error("转码过程异常: {}", e.getMessage(), e);
            if (tempFile.exists()) tempFile.delete();
            return null;
        }
    }

    public File ensureH264(File file) {
        if (!isVideoFile(file.getName())) {
            return file;
        }
        if (!needsTranscoding(file)) {
            return file;
        }
        return transcodeToH264(file);
    }
}
