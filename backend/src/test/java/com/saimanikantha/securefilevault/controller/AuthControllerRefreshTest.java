package com.saimanikantha.securefilevault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.constants.SecurityConstants;
import com.saimanikantha.securefilevault.dto.response.ActiveSessionResponse;
import com.saimanikantha.securefilevault.dto.response.TokenRefreshResponse;
import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.Role;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.GlobalExceptionHandler;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.security.JwtService;
import com.saimanikantha.securefilevault.security.RefreshCookieFactory;
import com.saimanikantha.securefilevault.security.RefreshTokenService;
import com.saimanikantha.securefilevault.security.SessionService;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import com.saimanikantha.securefilevault.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc test suite for verification of AuthController refresh, logout, and session management routes.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerRefreshTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private SessionService sessionService;

    @Mock
    private RefreshCookieFactory refreshCookieFactory;

    @Mock
    private AuthAuditService authAuditService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Mock method parameter resolver for UserDetails principal in standalone MockMvc setup
        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(UserDetails.class) 
                        && parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return org.springframework.security.core.userdetails.User.withUsername("testuser")
                        .password("password")
                        .roles("USER")
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void testRefreshSucceedsWithValidCookie() throws Exception {
        String rawToken = "raw-refresh-token";
        String newRawToken = "new-raw-refresh-token";
        String newAccessToken = "new-access-jwt-token";

        RefreshToken mockRotatedToken = RefreshToken.builder()
                .user(testUser)
                .tokenId("new-token-id")
                .tokenHash("new-hash")
                .familyId("family-123")
                .build();
        mockRotatedToken.setRawToken(newRawToken);

        ResponseCookie mockCookie = ResponseCookie.from("refresh_token", newRawToken)
                .path("/api/v1/auth")
                .httpOnly(true)
                .build();

        // Use any() for IP/UA strings since they may resolve to null or mocked servlet values
        when(refreshTokenService.rotate(eq(rawToken), any(), any())).thenReturn(mockRotatedToken);
        when(refreshCookieFactory.createCookie(newRawToken)).thenReturn(mockCookie);
        when(jwtService.generateToken(testUser)).thenReturn(newAccessToken);
        when(jwtProperties.getAccessExpirationMs()).thenReturn(900000L);

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", rawToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=" + newRawToken)))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/api/v1/auth")))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", is(newAccessToken)));
    }

    @Test
    void testRefreshFailsWithMissingCookie() throws Exception {
        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Refresh token is missing")));
    }

    @Test
    void testLogoutClearsCookieAndRevokesToken() throws Exception {
        String rawToken = "logout-refresh-token";
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        when(refreshCookieFactory.clearCookie()).thenReturn(clearCookie);

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", rawToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Path=/api/v1/auth")))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Logged out successfully")));

        verify(refreshTokenService).revokeToken(rawToken);
    }

    @Test
    void testGetActiveSessionsSucceeds() throws Exception {
        String rawToken = "session-refresh-token";
        String hashedToken = "hashed-session-token";
        
        ActiveSessionResponse activeSession = ActiveSessionResponse.builder()
                .id(1L)
                .deviceName("Chrome on Windows")
                .ipAddress("127.0.0.1")
                .currentSession(true)
                .build();

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));
        when(refreshTokenService.hashToken(rawToken)).thenReturn(hashedToken);
        when(sessionService.getActiveSessions(testUser, hashedToken)).thenReturn(List.of(activeSession));

        mockMvc.perform(get(ApiPaths.BASE_PATH + "/auth/sessions")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", rawToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].deviceName", is("Chrome on Windows")));
    }

    @Test
    void testRevokeSessionSucceeds() throws Exception {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete(ApiPaths.BASE_PATH + "/auth/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Session revoked successfully")));

        verify(sessionService).revokeSessionById(1L, testUser);
    }
}
