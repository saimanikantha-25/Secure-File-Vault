package com.saimanikantha.securefilevault.controller;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.constants.SecurityConstants;
import com.saimanikantha.securefilevault.dto.common.ApiResponse;
import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.dto.response.ActiveSessionResponse;
import com.saimanikantha.securefilevault.dto.response.LoginResponse;
import com.saimanikantha.securefilevault.dto.response.TokenRefreshResponse;
import com.saimanikantha.securefilevault.entity.AuthEventType;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.ResourceNotFoundException;
import com.saimanikantha.securefilevault.exception.TokenRefreshException;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.security.JwtService;
import com.saimanikantha.securefilevault.security.RefreshCookieFactory;
import com.saimanikantha.securefilevault.security.RefreshTokenService;
import com.saimanikantha.securefilevault.security.SessionService;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import com.saimanikantha.securefilevault.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user authentication lifecycle events: login, token rotation, logouts, active session list, and revocation.
 */
@RestController
@RequestMapping(ApiPaths.BASE_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final RefreshCookieFactory refreshCookieFactory;
    private final AuthAuditService authAuditService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        ResponseCookie cookie = refreshCookieFactory.createCookie(response.getRawRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("Authentication successful", response, HttpStatus.OK.value()));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken,
            HttpServletRequest request
    ) {
        if (rawRefreshToken == null || rawRefreshToken.trim().isEmpty()) {
            throw new TokenRefreshException("Refresh token is missing");
        }

        String ip = getClientIp(request);
        String ua = getUserAgent(request);

        RefreshToken rotatedToken = refreshTokenService.rotate(rawRefreshToken, ip, ua);
        ResponseCookie newCookie = refreshCookieFactory.createCookie(rotatedToken.getRawToken());

        String accessToken = jwtService.generateToken(rotatedToken.getUser());

        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .tokenType(SecurityConstants.TOKEN_TYPE)
                .expiresInMs(jwtProperties.getAccessExpirationMs())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(ApiResponse.success("Token refreshed successfully", response, HttpStatus.OK.value()));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken,
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String ip = getClientIp(request);
        String ua = getUserAgent(request);
        String username = userDetails != null ? userDetails.getUsername() : "unknown";

        if (rawRefreshToken != null && !rawRefreshToken.trim().isEmpty()) {
            refreshTokenService.revokeToken(rawRefreshToken);
            authAuditService.logEvent(username, AuthEventType.LOGOUT, ip, ua);
        }

        ResponseCookie clearCookie = refreshCookieFactory.clearCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResponse.success("Logged out successfully", null, HttpStatus.OK.value()));
    }

    @GetMapping("/auth/sessions")
    public ResponseEntity<ApiResponse<List<ActiveSessionResponse>>> getActiveSessions(
            @CookieValue(name = "refresh_token", required = false) String rawRefreshToken,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String currentTokenHash = refreshTokenService.hashToken(rawRefreshToken);
        List<ActiveSessionResponse> sessions = sessionService.getActiveSessions(user, currentTokenHash);

        return ResponseEntity.ok(ApiResponse.success("Active sessions retrieved", sessions, HttpStatus.OK.value()));
    }

    @DeleteMapping("/auth/sessions/{id}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        sessionService.revokeSessionById(id, user);

        return ResponseEntity.ok(ApiResponse.success("Session revoked successfully", null, HttpStatus.OK.value()));
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.trim().isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        return request.getHeader("User-Agent");
    }
}
