package com.fitness.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoRequest {
    @NotBlank(message = "视频标题不能为空")
    private String title;

    private String description;
    private String coverUrl;
    private String videoUrl;
    private String category;
    private Integer duration;
    private String difficulty;
    private String coach;
    private Integer sortOrder;
}
