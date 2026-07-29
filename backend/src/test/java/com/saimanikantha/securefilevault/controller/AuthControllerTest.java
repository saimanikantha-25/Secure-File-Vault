package com.saimanikantha.securefilevault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.entity.Role;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.security.jwt.secret=test_secret_that_must_be_at_least_32_characters_long_for_hmac_sha_256"
})
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and persist a test user
        User user = User.builder()
                .username("loginuser")
                .email("loginuser@example.com")
                .passwordHash(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void testLoginSuccessWithUsername() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .loginIdentifier("loginuser")
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.type", is("Bearer")))
                .andExpect(jsonPath("$.data.username", is("loginuser")))
                .andExpect(jsonPath("$.data.email", is("loginuser@example.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.expiresInMs", notNullValue()));
    }

    @Test
    void testLoginSuccessWithEmail() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .loginIdentifier("loginuser@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.type", is("Bearer")))
                .andExpect(jsonPath("$.data.email", is("loginuser@example.com")));
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .loginIdentifier("loginuser")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andExpect(jsonPath("$.message", is("Invalid username or password")))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    void testLoginUnknownUser() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .loginIdentifier("unknownuser")
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andExpect(jsonPath("$.message", is("Invalid username or password")))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    void testLoginValidationFailure() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .loginIdentifier("")
                .password("")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

}
