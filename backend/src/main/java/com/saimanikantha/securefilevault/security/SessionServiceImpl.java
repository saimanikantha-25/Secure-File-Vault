package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.dto.response.ActiveSessionResponse;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.ResourceNotFoundException;
import com.saimanikantha.securefilevault.exception.TokenRefreshException;
import com.saimanikantha.securefilevault.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Service implementation for managing active user sessions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActiveSessionResponse> getActiveSessions(User user, String currentTokenHash) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndRevokedFalseAndExpiryDateAfter(user, Instant.now());
        return activeTokens.stream()
                .map(token -> ActiveSessionResponse.builder()
                        .id(token.getId())
                        .deviceName(token.getDeviceName())
                        .ipAddress(token.getIpAddress())
                        .createdAt(token.getCreatedAt())
                        .lastUsedAt(token.getLastUsedAt())
                        .expiresAt(token.getExpiryDate())
                        .currentSession(token.getTokenHash().equals(currentTokenHash))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void revokeSessionById(Long id, User user) {
        RefreshToken token = refreshTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (!token.getUser().getId().equals(user.getId())) {
            throw new TokenRefreshException("Unauthorized to revoke this session", HttpStatus.FORBIDDEN);
        }
        token.setRevoked(true);
        token.setRevokedReason("USER_LOGOUT");
        token.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void cleanExpiredTokens(int retentionDays) {
        Instant retentionThreshold = Instant.now().minus(Duration.ofDays(retentionDays));
        Instant expiryThreshold = Instant.now();
        log.info("Pruning expired or revoked refresh tokens older than retention threshold: {}", retentionThreshold);
        refreshTokenRepository.deleteExpiredAndRevokedTokens(retentionThreshold, expiryThreshold);
    }
}
