package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

/**
 * Factory class to centralize HttpOnly refresh token cookie creation and management.
 */
@Component
@RequiredArgsConstructor
public class RefreshCookieFactory {

    private final JwtProperties jwtProperties;
    private final Environment environment;

    private static final String COOKIE_NAME = "refresh_token";
    private static final String COOKIE_PATH = "/api/v1/auth";

    /**
     * Creates an HttpOnly, SameSite=Strict refresh token cookie.
     *
     * @param rawToken the raw refresh token
     * @return the ResponseCookie
     */
    public ResponseCookie createCookie(String rawToken) {
        boolean secure = isSecureEnvironment();
        return ResponseCookie.from(COOKIE_NAME, rawToken)
                .httpOnly(true)
                .secure(secure)
                .path(COOKIE_PATH)
                .sameSite("Strict")
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpirationMs()))
                .build();
    }

    /**
     * Creates a cookie to clear the refresh token from the client.
     *
     * @return the ResponseCookie with maxAge=0
     */
    public ResponseCookie clearCookie() {
        boolean secure = isSecureEnvironment();
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .path(COOKIE_PATH)
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }

    private boolean isSecureEnvironment() {
        return Arrays.stream(environment.getActiveProfiles())
                .noneMatch(profile -> profile.equalsIgnoreCase("dev") || profile.equalsIgnoreCase("test"));
    }
}
