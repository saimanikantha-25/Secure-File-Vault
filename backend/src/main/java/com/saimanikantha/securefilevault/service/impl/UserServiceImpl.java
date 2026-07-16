package com.saimanikantha.securefilevault.service.impl;

import com.saimanikantha.securefilevault.dto.request.UserRegisterRequest;
import com.saimanikantha.securefilevault.dto.response.UserResponse;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.UserAlreadyExistsException;
import com.saimanikantha.securefilevault.mapper.UserMapper;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already registered.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

}
