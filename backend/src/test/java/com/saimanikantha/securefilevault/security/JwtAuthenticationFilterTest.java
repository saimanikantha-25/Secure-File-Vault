package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.entity.Role;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.jwt.secret=test_secret_that_must_be_at_least_32_characters_long_for_hmac_sha_256",
        "app.security.jwt.issuer=SecureFileVault"
})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .username("filteruser")
                .email("filteruser@example.com")
                .passwordHash(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void testValidJwtAuthenticatesRequest() throws Exception {
        String token = jwtService.generateToken(testUser);

        mockMvc.perform(get("/api/v1/test/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("Protected Data")));
    }

    @Test
    void testMissingHeaderContinuesUnauthenticated() throws Exception {
        // Without header, protected path must trigger 401 authentication entry point
        mockMvc.perform(get("/api/v1/test/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Authentication required")));
    }

    @Test
    void testInvalidJwtReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/test/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)));
    }

    @Test
    void testExpiredJwtReturns401() throws Exception {
        long originalExpiration = jwtProperties.getExpirationMs();
        try {
            // Set negative expiration to generate an already expired token
            jwtProperties.setExpirationMs(-60000);
            
            // Re-initialize JwtService signing properties temporarily if needed, 
            // but generateToken reads expirationMs directly from properties.
            String expiredToken = jwtService.generateToken(testUser);

            mockMvc.perform(get("/api/v1/test/protected")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.statusCode", is(401)));
        } finally {
            // Restore configuration state
            jwtProperties.setExpirationMs(originalExpiration);
        }
    }

    @Test
    void testTamperedJwtReturns401() throws Exception {
        String token = jwtService.generateToken(testUser);
        String tamperedToken = token + "modified";

        mockMvc.perform(get("/api/v1/test/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)));
    }

    @Test
    void testMalformedJwtReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/test/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)));
    }

    @Test
    void testPublicEndpointsAccessibleWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("Public Data")));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointRejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/test/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andExpect(jsonPath("$.message", is("Authentication required")));
    }
}
