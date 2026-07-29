package com.saimanikantha.securefilevault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Data Transfer Object representing an active user device session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSessionResponse {

    private Long id;
    private String deviceName;
    private String ipAddress;
    private Instant lastUsedAt;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean currentSession;

}
