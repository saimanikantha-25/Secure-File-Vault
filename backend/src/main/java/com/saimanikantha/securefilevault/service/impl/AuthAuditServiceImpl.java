package com.saimanikantha.securefilevault.service.impl;

import com.saimanikantha.securefilevault.entity.AuthAuditLog;
import com.saimanikantha.securefilevault.entity.AuthEventType;
import com.saimanikantha.securefilevault.entity.User;
import com.saimanikantha.securefilevault.repository.AuthAuditLogRepository;
import com.saimanikantha.securefilevault.repository.UserRepository;
import com.saimanikantha.securefilevault.service.AuthAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for recording authentication events in audit tables.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthAuditServiceImpl implements AuthAuditService {

    private final AuthAuditLogRepository authAuditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Async
    @Transactional
    public void logEvent(String username, AuthEventType eventType, String ipAddress, String userAgent) {
        log.info("Logging auth event: {} for user: {} [IP: {}]", eventType, username, ipAddress);

        User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);

        AuthAuditLog auditLog = AuthAuditLog.builder()
                .user(user)
                .username(username)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        authAuditLogRepository.save(auditLog);
    }
}
