package com.saimanikantha.securefilevault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.dto.request.UserRegisterRequest;
import com.saimanikantha.securefilevault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUserSuccess() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("validuser")
                .email("validuser@example.com")
                .password("Password123!")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("registered successfully")))
                .andExpect(jsonPath("$.statusCode", is(201)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.username", is("validuser")))
                .andExpect(jsonPath("$.data.email", is("validuser@example.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.createdAt", notNullValue()));
    }

    @Test
    void testRegisterUserValidationFailure() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("") // Blank
                .email("invalid-email") // Invalid format
                .password("short") // Less than 8 characters
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message").value("Request validation failed."))
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.data.username", notNullValue()))
                .andExpect(jsonPath("$.data.email", notNullValue()))
                .andExpect(jsonPath("$.data.password", notNullValue()));
    }

    @Test
    void testRegisterUserDuplicateUsernameConflict() throws Exception {
        UserRegisterRequest request1 = UserRegisterRequest.builder()
                .username("duplicateuser")
                .email("user1@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        UserRegisterRequest request2 = UserRegisterRequest.builder()
                .username("duplicateuser") // Same username
                .email("user2@example.com") // Different email
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(409)))
                .andExpect(jsonPath("$.message", containsString("Username 'duplicateuser' is already registered")));
    }

    @Test
    void testRegisterUserDuplicateEmailConflict() throws Exception {
        UserRegisterRequest request1 = UserRegisterRequest.builder()
                .username("user1")
                .email("duplicate@example.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        UserRegisterRequest request2 = UserRegisterRequest.builder()
                .username("user2") // Different username
                .email("duplicate@example.com") // Same email
                .password("Password123!")
                .build();

        mockMvc.perform(post(ApiPaths.BASE_PATH + "/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.statusCode", is(409)))
                .andExpect(jsonPath("$.message", containsString("Email 'duplicate@example.com' is already registered")));
    }

}
