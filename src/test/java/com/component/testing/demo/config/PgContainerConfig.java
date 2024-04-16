package com.component.testing.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class PgContainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer getPostgres() {
        return new PostgreSQLContainer<>("postgres:15-alpine")
            .withCopyToContainer(
                MountableFile.forClasspathResource("comment_schema.sql"),
                "/docker-entrypoint-initdb.d/comment_schema.sql"
            )
            .withLabel("com.testcontainers.desktop.service", "postgres");
    }

}
