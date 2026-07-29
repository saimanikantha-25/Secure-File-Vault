package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.config.RefreshProperties;
import com.saimanikantha.securefilevault.entity.AuthEventType;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.TokenRefreshException;
import com.saimanikantha.securefilevault.repository.RefreshTokenRepository;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing refresh token operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshProperties refreshProperties;
    private final JwtProperties jwtProperties;
    private final AuthAuditService authAuditService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Parser uaParser = new Parser();

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress, String userAgent, String familyId, String parentTokenId) {
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);
        String finalFamilyId = (familyId == null) ? UUID.randomUUID().toString() : familyId;
        String tokenId = UUID.randomUUID().toString();
        String deviceName = parseDeviceName(userAgent);

        Instant expiryDate = Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenId(tokenId)
                .tokenHash(tokenHash)
                .familyId(finalFamilyId)
                .parentTokenId(parentTokenId)
                .expiryDate(expiryDate)
                .revoked(false)
                .reuseDetected(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceName(deviceName)
                .lastUsedAt(Instant.now())
                .build();

        // Storing transient raw token on the entity to return to controller
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        savedToken.setRawToken(rawToken);
        
        return savedToken;
    }

    @Override
    @Transactional
    public RefreshToken rotate(String rawToken, String ipAddress, String userAgent) {
        String tokenHash = hashToken(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    authAuditService.logEvent("unknown", AuthEventType.REFRESH_FAILURE, ipAddress, userAgent);
                    return new TokenRefreshException("Invalid refresh token");
                });

        if (token.getExpiryDate().isBefore(Instant.now())) {
            authAuditService.logEvent(token.getUser().getUsername(), AuthEventType.REFRESH_FAILURE, ipAddress, userAgent);
            throw new TokenRefreshException("Refresh token has expired");
        }

        // Reuse Detection (RTR Breach)
        if (token.isRevoked() || token.isReuseDetected()) {
            handleTokenReuse(token, ipAddress, userAgent);
            throw new TokenRefreshException("Invalid refresh token");
        }

        // Mark current token rotated/used
        token.setRevoked(true);
        token.setRevokedReason("ROTATED");
        token.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(token);

        // Create new rotated token in the same family (sliding window)
        // We need to return the new token. We'll generate a new raw token.
        RefreshToken newToken = createRefreshToken(
                token.getUser(),
                ipAddress,
                userAgent,
                token.getFamilyId(),
                token.getTokenId()
        );

        authAuditService.logEvent(token.getUser().getUsername(), AuthEventType.REFRESH_SUCCESS, ipAddress, userAgent);
        return newToken;
    }

    @Override
    @Transactional
    public void revokeToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setRevokedReason("USER_LOGOUT");
                    token.setLastUsedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    public String hashToken(String rawToken) {
        if (rawToken == null) {
            return null;
        }
        try {
            String secret = getSecret();
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hashBytes = sha256Hmac.doFinal(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            log.error("Failed to compute HMAC-SHA256 hash for refresh token", e);
            throw new RuntimeException("Cryptographic hashing failed", e);
        }
    }

    private void handleTokenReuse(RefreshToken token, String ipAddress, String userAgent) {
        log.warn("Refresh token reuse detected for user: {}, family: {}! Revoking entire family.", 
                token.getUser().getUsername(), token.getFamilyId());
        
        authAuditService.logEvent(
                token.getUser().getUsername(),
                AuthEventType.TOKEN_REUSE_DETECTED,
                ipAddress,
                userAgent
        );

        List<RefreshToken> familyTokens = refreshTokenRepository.findByFamilyId(token.getFamilyId());
        for (RefreshToken t : familyTokens) {
            t.setRevoked(true);
            t.setReuseDetected(true);
            t.setRevokedReason("TOKEN_REUSE");
            t.setLastUsedAt(Instant.now());
        }
        refreshTokenRepository.saveAll(familyTokens);
    }

    private String generateRawToken() {
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String parseDeviceName(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return "Unknown Device";
        }
        try {
            Client c = uaParser.parse(userAgent);
            String browser = c.userAgent.family;
            String os = c.os.family;
            if (browser == null || browser.equalsIgnoreCase("other")) {
                browser = "Unknown Browser";
            }
            if (os == null || os.equalsIgnoreCase("other")) {
                os = "Unknown OS";
            }
            return browser + " on " + os;
        } catch (Exception e) {
            log.warn("User-Agent parser failed. Defaulting to Unknown Device. Error: {}", e.getMessage());
            return "Unknown Device";
        }
    }

    private String getSecret() {
        String secret = refreshProperties.getHmacSecret();
        if (secret == null || secret.trim().isEmpty()) {
            log.warn("HMAC secret for refresh tokens is not set. Using derived fallback.");
            return jwtProperties.getSecret() + "_refresh";
        }
        return secret;
    }
}
