package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("checkins")
public class Checkin {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private Integer duration;
    private String type;
    private String intensity;
    private String content;
    private Integer caloriesBurned;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
