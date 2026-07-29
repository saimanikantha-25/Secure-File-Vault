package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.RefreshProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled runner to clean up expired and revoked refresh tokens from the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final SessionService sessionService;
    private final RefreshProperties refreshProperties;

    /**
     * Triggers token cleanup at intervals determined by app.security.jwt.cleanup-cron.
     */
    @Scheduled(cron = "${app.security.jwt.cleanup-cron:0 0 3 * * *}")
    public void cleanExpiredTokens() {
        log.info("Scheduled task triggered: Pruning expired/revoked refresh tokens.");
        try {
            sessionService.cleanExpiredTokens(refreshProperties.getRetentionDays());
            log.info("Pruning task finished successfully.");
        } catch (Exception e) {
            log.error("Failed to run scheduled token pruning job", e);
        }
    }
}
