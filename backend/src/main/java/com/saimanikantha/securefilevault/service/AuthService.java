package com.saimanikantha.securefilevault.service;

import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}
