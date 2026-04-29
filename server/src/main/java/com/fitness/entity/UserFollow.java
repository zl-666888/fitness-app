package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_follows")
public class UserFollow {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer followerId;
    private Integer followingId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
