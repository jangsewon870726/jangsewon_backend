package com.wirebarley.transfer.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
// infra 모듈의 리포지토리만 스캔하도록 경로를 지정.
@EnableJpaRepositories(basePackages = "com.wirebarley.transfer.infra.repository")
// infra 모듈의 엔티티만 스캔하도록 경로를 지정.
@EntityScan(basePackages = "com.wirebarley.transfer.infra.entity")
public class JpaConfig {
}