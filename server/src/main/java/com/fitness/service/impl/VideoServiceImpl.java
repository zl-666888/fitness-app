package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.VideoRequest;
import com.fitness.entity.Video;
import com.fitness.entity.VideoFavorite;
import com.fitness.mapper.VideoFavoriteMapper;
import com.fitness.mapper.VideoMapper;
import com.fitness.service.VideoService;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;
    private final VideoFavoriteMapper videoFavoriteMapper;

    public VideoServiceImpl(VideoMapper videoMapper, VideoFavoriteMapper videoFavoriteMapper) {
        this.videoMapper = videoMapper;
        this.videoFavoriteMapper = videoFavoriteMapper;
    }

    @Override
    public IPage<Video> getVideoList(Integer page, Integer size, String category, String difficulty) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Video::getCategory, category);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Video::getDifficulty, difficulty);
        }
        wrapper.orderByAsc(Video::getSortOrder).orderByDesc(Video::getCreatedAt);
        return videoMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Video getVideoDetail(Integer videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw BizException.notFound("视频不存在");
        }
        return video;
    }

    @Override
    public IPage<Video> searchVideos(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Video::getTitle, keyword)
                    .or().like(Video::getDescription, keyword)
                    .or().like(Video::getCoach, keyword);
        }
        wrapper.orderByAsc(Video::getSortOrder).orderByDesc(Video::getCreatedAt);
        return videoMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void toggleFavorite(Integer userId, Integer videoId) {
        if (videoMapper.selectById(videoId) == null) {
            throw BizException.notFound("视频不存在");
        }
        VideoFavorite fav = videoFavoriteMapper.selectOne(
                new LambdaQueryWrapper<VideoFavorite>()
                        .eq(VideoFavorite::getUserId, userId)
                        .eq(VideoFavorite::getVideoId, videoId));
        if (fav == null) {
            fav = new VideoFavorite();
            fav.setUserId(userId);
            fav.setVideoId(videoId);
            videoFavoriteMapper.insert(fav);
        } else {
            videoFavoriteMapper.deleteById(fav.getId());
        }
    }

    @Override
    public boolean isFavorited(Integer userId, Integer videoId) {
        return videoFavoriteMapper.selectCount(
                new LambdaQueryWrapper<VideoFavorite>()
                        .eq(VideoFavorite::getUserId, userId)
                        .eq(VideoFavorite::getVideoId, videoId)) > 0;
    }

    @Override
    public IPage<Video> getFavoriteList(Integer userId, Integer page, Integer size) {
        IPage<VideoFavorite> favPage = videoFavoriteMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<VideoFavorite>()
                        .eq(VideoFavorite::getUserId, userId)
                        .orderByDesc(VideoFavorite::getCreatedAt));

        IPage<Video> result = new Page<>(favPage.getCurrent(), favPage.getSize(), favPage.getTotal());
        result.setRecords(favPage.getRecords().stream()
                .map(f -> videoMapper.selectById(f.getVideoId()))
                .collect(java.util.stream.Collectors.toList()));
        return result;
    }

    @Override
    public Video createVideo(VideoRequest request) {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setCoverUrl(request.getCoverUrl());
        video.setVideoUrl(request.getVideoUrl());
        video.setCategory(request.getCategory());
        video.setDuration(request.getDuration());
        video.setDifficulty(request.getDifficulty());
        video.setCoach(request.getCoach());
        video.setSortOrder(request.getSortOrder());
        video.setStatus(1);
        videoMapper.insert(video);
        return video;
    }

    @Override
    public Video updateVideo(Integer videoId, VideoRequest request) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw BizException.notFound("视频不存在");
        }
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setCoverUrl(request.getCoverUrl());
        video.setVideoUrl(request.getVideoUrl());
        video.setCategory(request.getCategory());
        video.setDuration(request.getDuration());
        video.setDifficulty(request.getDifficulty());
        video.setCoach(request.getCoach());
        video.setSortOrder(request.getSortOrder());
        videoMapper.updateById(video);
        return video;
    }

    @Override
    public void deleteVideo(Integer videoId) {
        if (videoMapper.selectById(videoId) == null) {
            throw BizException.notFound("视频不存在");
        }
        videoMapper.deleteById(videoId);
    }

    @Override
    public long countTotalVideos() {
        return videoMapper.selectCount(null);
    }
}
