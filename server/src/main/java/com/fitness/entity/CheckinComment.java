package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin_comments")
public class CheckinComment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer checkinId;
    private Integer userId;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
