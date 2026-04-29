package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("user_goals")
public class UserGoal {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String goalType;
    private BigDecimal targetWeight;
    private Integer weeklyFrequency;
    private LocalTime reminderTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
