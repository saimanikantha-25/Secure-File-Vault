package com.saimanikantha.securefilevault.service;

import com.saimanikantha.securefilevault.dto.request.UserRegisterRequest;
import com.saimanikantha.securefilevault.dto.response.UserResponse;

public interface UserService {

    UserResponse register(UserRegisterRequest request);

}
