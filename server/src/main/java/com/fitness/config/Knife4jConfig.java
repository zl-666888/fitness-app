package com.fitness.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("健身打卡小程序 API")
                        .description("健身打卡小程序后端接口文档<br/>" +
                                "使用说明：<br/>" +
                                "1. 先调用 用户端-认证/register 或 /login 获取 token<br/>" +
                                "2. 点击右上角 Authorize 按钮，粘贴 token 到 Bearer Token<br/>" +
                                "3. 即可测试其他需要认证的接口<br/>" +
                                "管理端同理，先调用 管理端-认证/login 获取管理员 token")
                        .contact(new Contact().name("开发团队"))
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
