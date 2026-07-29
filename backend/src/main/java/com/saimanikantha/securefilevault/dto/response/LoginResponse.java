package com.saimanikantha.securefilevault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type; // SecurityConstants.TOKEN_TYPE
    private long expiresInMs;
    private String username;
    private String email;
    private String role;

}
