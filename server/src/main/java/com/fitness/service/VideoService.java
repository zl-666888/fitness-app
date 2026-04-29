package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.VideoRequest;
import com.fitness.entity.Video;

public interface VideoService {
    IPage<Video> getVideoList(Integer page, Integer size, String category, String difficulty);
    Video getVideoDetail(Integer videoId);
    IPage<Video> searchVideos(Integer page, Integer size, String keyword);
    void toggleFavorite(Integer userId, Integer videoId);
    boolean isFavorited(Integer userId, Integer videoId);
    IPage<Video> getFavoriteList(Integer userId, Integer page, Integer size);
    Video createVideo(VideoRequest request);
    Video updateVideo(Integer videoId, VideoRequest request);
    void deleteVideo(Integer videoId);
    long countTotalVideos();
}
