package com.saimanikantha.securefilevault.controller;

import com.saimanikantha.securefilevault.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> testPublic() {
        return ResponseEntity.ok(ApiResponse.success("Public endpoint access successful", "Public Data", 200));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> testProtected() {
        return ResponseEntity.ok(ApiResponse.success("Protected endpoint access successful", "Protected Data", 200));
    }
}
