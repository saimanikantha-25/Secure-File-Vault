package com.saimanikantha.securefilevault.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for refresh token settings, prefixed with app.security.refresh.
 */
@Configuration
@ConfigurationProperties(prefix = "app.security.refresh")
@Data
public class RefreshProperties {

    private String hmacSecret;
    private int retentionDays = 30;

}
