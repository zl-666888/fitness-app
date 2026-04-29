package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("videos")
public class Video {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String description;
    private String coverUrl;
    private String videoUrl;
    private String category;
    private Integer duration;
    private String difficulty;
    private String coach;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
