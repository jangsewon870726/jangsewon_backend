package com.wirebarley.transfer.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories(basePackages = "com.wirebarley.transfer.infra.repository")
@EntityScan(basePackages = "com.wirebarley.transfer.infra.entity")
public class JpaConfig {
}