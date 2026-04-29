package com.fitness.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class FeedResponse {
    private Integer checkinId;
    private Integer userId;
    private String nickname;
    private String avatar;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private Integer duration;
    private String type;
    private String intensity;
    private String content;
    private Integer caloriesBurned;
    private long likeCount;
    private long commentCount;
    private boolean liked;
    private LocalDateTime createdAt;
}
