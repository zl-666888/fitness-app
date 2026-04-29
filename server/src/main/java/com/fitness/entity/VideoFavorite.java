package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_favorites")
public class VideoFavorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer videoId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
