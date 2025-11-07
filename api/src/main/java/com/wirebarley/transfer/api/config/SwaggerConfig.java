package com.wirebarley.transfer.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("와이어바알리 송금 서비스 API")
                .version("v1.0.0")
                .description("계좌 잔액 조회 및 송금 기능을 제공하는 API 명세서");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}