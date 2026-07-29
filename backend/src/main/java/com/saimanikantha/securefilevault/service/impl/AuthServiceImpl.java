package com.saimanikantha.securefilevault.service.impl;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.constants.SecurityConstants;
import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.dto.response.LoginResponse;
import com.saimanikantha.securefilevault.entity.AuthEventType;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.InvalidCredentialsException;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.security.JwtService;
import com.saimanikantha.securefilevault.security.RefreshTokenService;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import com.saimanikantha.securefilevault.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service implementation for handling user authentication, refresh tokens, and audit events.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final AuthAuditService authAuditService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getLoginIdentifier();
        HttpServletRequest currentRequest = getCurrentRequest();
        String ip = getClientIp(currentRequest);
        String ua = getUserAgent(currentRequest);

        try {
            // Perform single query lookup to find user by username OR email
            User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                    .orElseThrow(InvalidCredentialsException::new);

            // Verify password using BCrypt matches
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new InvalidCredentialsException();
            }

            // Generate access token
            String accessToken = jwtService.generateToken(user);

            // Generate refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, ip, ua, null, null);

            // Log login success audit event
            authAuditService.logEvent(user.getUsername(), AuthEventType.LOGIN_SUCCESS, ip, ua);

            return LoginResponse.builder()
                    .token(accessToken)
                    .type(SecurityConstants.TOKEN_TYPE)
                    .expiresInMs(jwtProperties.getAccessExpirationMs())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .rawRefreshToken(refreshToken.getRawToken())
                    .build();

        } catch (InvalidCredentialsException e) {
            // Log login failure audit event
            authAuditService.logEvent(identifier, AuthEventType.LOGIN_FAILURE, ip, ua);
            throw e;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
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
