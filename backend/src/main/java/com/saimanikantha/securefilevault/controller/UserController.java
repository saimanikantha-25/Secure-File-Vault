package com.saimanikantha.securefilevault.controller;

import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.dto.common.ApiResponse;
import com.saimanikantha.securefilevault.dto.request.UserRegisterRequest;
import com.saimanikantha.securefilevault.dto.response.UserResponse;
import com.saimanikantha.securefilevault.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.BASE_PATH)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully", response, HttpStatus.CREATED.value()),
                HttpStatus.CREATED
        );
    }

}
