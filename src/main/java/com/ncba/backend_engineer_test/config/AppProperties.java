package com.ncba.backend_engineer_test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "settings")
@Data
public class AppProperties {
    // Sensible defaults so the app can start even if externalized properties are missing
    private Integer connectionTimeoutInSeconds = 25; // eg 25 (seconds)
    private Integer maxLifeTimeInSeconds = 300; // eg 300 (5 minutes)
    private Integer readTimeoutInSeconds = 25; // eg 25 (seconds)
    private Integer maxConnections = 500; // eg 500 (max connections)
    private Integer pendingAcquireMaxCount = 1000; // eg 1000 (pending connections)
    private Integer pendingAcquireTimeoutInSeconds = 10; // eg 10 (seconds)

    private String soapEndpoint;

}
