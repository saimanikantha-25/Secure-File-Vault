package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.dto.response.ActiveSessionResponse;
import com.saimanikantha.securefilevault.entity.User;

import java.util.List;

/**
 * Service interface for managing active user sessions (active refresh tokens).
 */
public interface SessionService {

    /**
     * Retrieves all active sessions (non-revoked, non-expired tokens) for a given user.
     *
     * @param user             the owner of the sessions
     * @param currentTokenHash the HMAC hash of the client's current refresh token to flag current session
     * @return a list of active sessions mapped as DTOs
     */
    List<ActiveSessionResponse> getActiveSessions(User user, String currentTokenHash);

    /**
     * Revokes a specific active session by its database record identifier.
     *
     * @param id   the session record identifier
     * @param user the user requesting the revocation (for ownership validation)
     */
    void revokeSessionById(Long id, User user);

    /**
     * Scheduled cleanup job that permanently prunes expired or revoked tokens past their retention window.
     *
     * @param retentionDays the duration in days to retain records for forensic auditing
     */
    void cleanExpiredTokens(int retentionDays);
}
