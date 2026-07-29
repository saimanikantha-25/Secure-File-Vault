package com.saimanikantha.securefilevault.service;

import com.saimanikantha.securefilevault.entity.AuthEventType;

/**
 * Service interface for logging authentication audit events.
 */
public interface AuthAuditService {

    /**
     * Persists an authentication audit event record.
     *
     * @param username  the username associated with the event
     * @param eventType the type of event (login success, failure, rotation, etc.)
     * @param ipAddress the client's IP address
     * @param userAgent the client's User-Agent string
     */
    void logEvent(String username, AuthEventType eventType, String ipAddress, String userAgent);
}
