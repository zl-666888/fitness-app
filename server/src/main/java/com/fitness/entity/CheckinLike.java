package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin_likes")
public class CheckinLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer checkinId;
    private Integer userId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
