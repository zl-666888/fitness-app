package com.fitness.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "打卡请求")
public class CheckinRequest {
    @NotNull(message = "打卡日期不能为空")
    @Schema(description = "打卡日期", example = "2026-04-27")
    private LocalDate checkinDate;

    @Schema(description = "打卡时间", example = "08:30:00")
    private LocalTime checkinTime;

    @Schema(description = "运动时长(分钟)", example = "30")
    private Integer duration;

    @Schema(description = "运动类型", example = "跑步")
    private String type;

    @Schema(description = "运动强度: low/medium/high", example = "medium")
    private String intensity;

    @Schema(description = "备注")
    private String content;

    @Schema(description = "消耗卡路里(kcal)", example = "200")
    private Integer caloriesBurned;
}
