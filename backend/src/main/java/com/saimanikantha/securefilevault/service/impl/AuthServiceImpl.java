package com.saimanikantha.securefilevault.service.impl;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.constants.SecurityConstants;
import com.saimanikantha.securefilevault.dto.request.LoginRequest;
import com.saimanikantha.securefilevault.dto.response.LoginResponse;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.exception.InvalidCredentialsException;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.security.JwtService;
import com.saimanikantha.securefilevault.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getLoginIdentifier();

        // Perform single query lookup to find user by username OR email
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(InvalidCredentialsException::new);

        // Verify password using BCrypt matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Generate token using the User object
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .type(SecurityConstants.TOKEN_TYPE)
                .expiresInMs(jwtProperties.getExpirationMs())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

}
