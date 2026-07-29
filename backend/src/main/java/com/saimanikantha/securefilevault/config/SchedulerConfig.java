package com.saimanikantha.securefilevault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduled execution and asynchronous task processing.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {
}
