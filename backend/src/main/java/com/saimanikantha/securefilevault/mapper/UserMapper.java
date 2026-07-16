package com.saimanikantha.securefilevault.mapper;

import com.saimanikantha.securefilevault.dto.request.UserRegisterRequest;
import com.saimanikantha.securefilevault.dto.response.UserResponse;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(UserRegisterRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER) // Default role for registering users
                .build();
    }

}
