package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.config.RefreshProperties;
import com.saimanikantha.securefilevault.entity.AuthEventType;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.TokenRefreshException;
import com.saimanikantha.securefilevault.repository.RefreshTokenRepository;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RefreshTokenServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshProperties refreshProperties;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private AuthAuditService authAuditService;

    private RefreshTokenServiceImpl refreshTokenService;
    private User testUser;
    private static final String TEST_SECRET = "refresh_hmac_secret_key_which_is_long_and_secure_32_bytes";

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(
                refreshTokenRepository,
                refreshProperties,
                jwtProperties,
                authAuditService
        );
        lenient().when(refreshProperties.getHmacSecret()).thenReturn(TEST_SECRET);
        lenient().when(jwtProperties.getRefreshExpirationMs()).thenReturn(604800000L); // 7 days

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .build();
    }

    @Test
    void testCreateRefreshTokenHashesCorrectly() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(
                testUser, "127.0.0.1", "Mozilla/5.0", null, null
        );

        assertThat(token).isNotNull();
        assertThat(token.getRawToken()).isNotBlank();
        assertThat(token.getTokenHash()).isEqualTo(refreshTokenService.hashToken(token.getRawToken()));
        assertThat(token.getFamilyId()).isNotBlank();
        assertThat(token.getDeviceName()).contains("Unknown Browser on Unknown OS");
    }

    @Test
    void testRotateValidTokenSucceeds() {
        String rawToken = "myrawtokenstringthatissecure";
        String hashedToken = refreshTokenService.hashToken(rawToken);

        RefreshToken mockToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .tokenHash(hashedToken)
                .familyId("family-123")
                .tokenId("token-123")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .reuseDetected(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(mockToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken rotated = refreshTokenService.rotate(rawToken, "127.0.0.1", "Mozilla/5.0");

        assertThat(rotated).isNotNull();
        verify(refreshTokenRepository, times(1)).save(mockToken);
        assertThat(mockToken.isRevoked()).isTrue();
        assertThat(mockToken.getRevokedReason()).isEqualTo("ROTATED");
        verify(authAuditService).logEvent(eq("testuser"), eq(AuthEventType.REFRESH_SUCCESS), anyString(), anyString());
    }

    @Test
    void testRotateExpiredTokenThrowsException() {
        String rawToken = "expiredrawtoken";
        String hashedToken = refreshTokenService.hashToken(rawToken);

        RefreshToken mockToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .tokenHash(hashedToken)
                .familyId("family-123")
                .expiryDate(Instant.now().minusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(mockToken));

        assertThatThrownBy(() -> refreshTokenService.rotate(rawToken, "127.0.0.1", "Mozilla/5.0"))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("expired");

        verify(authAuditService).logEvent(eq("testuser"), eq(AuthEventType.REFRESH_FAILURE), anyString(), anyString());
    }

    @Test
    void testRotateAlreadyRevokedTokenTriggersRTRBreach() {
        String rawToken = "reusedrawtoken";
        String hashedToken = refreshTokenService.hashToken(rawToken);

        RefreshToken mockToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .tokenHash(hashedToken)
                .familyId("family-123")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByTokenHash(hashedToken)).thenReturn(Optional.of(mockToken));
        when(refreshTokenRepository.findByFamilyId("family-123")).thenReturn(Collections.singletonList(mockToken));

        assertThatThrownBy(() -> refreshTokenService.rotate(rawToken, "127.0.0.1", "Mozilla/5.0"))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Invalid refresh token");

        assertThat(mockToken.isReuseDetected()).isTrue();
        assertThat(mockToken.getRevokedReason()).isEqualTo("TOKEN_REUSE");
        verify(authAuditService).logEvent(eq("testuser"), eq(AuthEventType.TOKEN_REUSE_DETECTED), anyString(), anyString());
    }
}
