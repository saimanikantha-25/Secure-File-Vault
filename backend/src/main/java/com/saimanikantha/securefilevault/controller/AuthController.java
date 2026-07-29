package com.saimanikantha.securefilevault.controller;

import com.saimanikantha.securefilevault.constants.ApiPaths;
import com.saimanikantha.securefilevault.dto.common.ApiResponse;
import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.dto.response.LoginResponse;
import com.saimanikantha.securefilevault.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return new ResponseEntity<>(
                ApiResponse.success("Authentication successful", response, HttpStatus.OK.value()),
                HttpStatus.OK
        );
    }

}
