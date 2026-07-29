package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.entity.Role;
import com.saimanikantha.securefilevault.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties;
    private User testUser;
    private static final String VALID_SECRET = "my_super_secret_key_of_at_least_32_characters_long_for_hmac_256";
    private static final String VALID_ISSUER = "SecureFileVault";

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(VALID_SECRET);
        jwtProperties.setExpirationMs(3600000); // 1 hour
        jwtProperties.setIssuer(VALID_ISSUER);

        jwtService = new JwtService(jwtProperties);
        jwtService.init();

        testUser = User.builder()
                .username("user1")
                .email("user1@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void testStartupValidationFailsIfSecretTooShort() {
        JwtProperties badProperties = new JwtProperties();
        badProperties.setSecret("short_key");
        badProperties.setExpirationMs(3600000);
        badProperties.setIssuer(VALID_ISSUER);

        JwtService badService = new JwtService(badProperties);
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 256 bits");
    }

    @Test
    void testStartupValidationFailsIfSecretNullOrEmpty() {
        JwtProperties badProperties = new JwtProperties();
        badProperties.setSecret("");
        badProperties.setExpirationMs(3600000);
        badProperties.setIssuer(VALID_ISSUER);

        JwtService badService = new JwtService(badProperties);
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not be null or empty");
    }

    @Test
    void testStartupValidationFailsIfExpirationNonPositive() {
        JwtProperties badProperties = new JwtProperties();
        badProperties.setSecret(VALID_SECRET);
        badProperties.setExpirationMs(0); // zero
        badProperties.setIssuer(VALID_ISSUER);

        JwtService badService = new JwtService(badProperties);
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expiration duration must be greater than zero");

        badProperties.setExpirationMs(-60000); // negative
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expiration duration must be greater than zero");
    }

    @Test
    void testStartupValidationFailsIfIssuerNullOrEmpty() {
        JwtProperties badProperties = new JwtProperties();
        badProperties.setSecret(VALID_SECRET);
        badProperties.setExpirationMs(3600000);
        badProperties.setIssuer(""); // empty

        JwtService badService = new JwtService(badProperties);
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("issuer must not be null or empty");

        badProperties.setIssuer(null); // null
        assertThatThrownBy(badService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("issuer must not be null or empty");
    }

    @Test
    void testValidTokenGenerationAndClaimsExtraction() {
        String token = jwtService.generateToken(testUser);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user1");
        assertThat(jwtService.extractRole(token)).isEqualTo("USER");
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    void testExpirationParsing() {
        String token = jwtService.generateToken(testUser);
        Instant expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isAfter(Instant.now());
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    void testExpiredTokenRejection() {
        // Set short expiration
        jwtProperties.setExpirationMs(1); // 1 ms
        jwtService = new JwtService(jwtProperties);
        jwtService.init();

        String token = jwtService.generateToken(testUser);

        // Sleep to ensure expiration
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(jwtService.isTokenExpired(token)).isTrue();
        assertThat(jwtService.isTokenValid(token, testUser)).isFalse();
    }

    @Test
    void testTamperedTokenRejection() {
        String token = jwtService.generateToken(testUser);
        String tamperedToken = token + "modified";

        assertThat(jwtService.isTokenValid(tamperedToken, testUser)).isFalse();
    }

    @Test
    void testMalformedTokenRejection() {
        String malformedToken = "not.a.jwt.token";
        assertThat(jwtService.isTokenValid(malformedToken, testUser)).isFalse();
    }

    @Test
    void testInvalidSignatureRejection() {
        // Generate token with another key
        JwtProperties otherProperties = new JwtProperties();
        otherProperties.setSecret("another_super_secret_key_of_at_least_32_characters");
        otherProperties.setExpirationMs(3600000);
        otherProperties.setIssuer(VALID_ISSUER);
        JwtService otherService = new JwtService(otherProperties);
        otherService.init();

        String tokenFromOtherKey = otherService.generateToken(testUser);

        // Validating with main service should fail signature verification
        assertThat(jwtService.isTokenValid(tokenFromOtherKey, testUser)).isFalse();
    }
}
