package com.fitness.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    @Schema(description = "访问令牌（有效期2小时）")
    private String accessToken;

    @Schema(description = "刷新令牌（有效期7天）")
    private String refreshToken;

    @Schema(description = "用户信息")
    private Object user;
}
