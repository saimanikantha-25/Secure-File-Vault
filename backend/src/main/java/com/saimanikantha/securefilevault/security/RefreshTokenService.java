package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;

/**
 * Service interface for handling refresh token cryptographic generation, hashing, rotation, and revocation.
 */
public interface RefreshTokenService {

    /**
     * Generates a new refresh token, hashes it, and persists the record.
     *
     * @param user          the owner of the refresh token
     * @param ipAddress     the client's IP address
     * @param userAgent     the client's User-Agent string
     * @param familyId      the token family identifier (generates new one if null)
     * @param parentTokenId the parent token's identifier (null for initial logins)
     * @return the created RefreshToken entity
     */
    RefreshToken createRefreshToken(User user, String ipAddress, String userAgent, String familyId, String parentTokenId);

    /**
     * Rotates a refresh token: verifies validity, checks reuse, marks old token as revoked,
     * and generates a new token under the same family.
     *
     * @param rawToken  the raw refresh token presented by the client
     * @param ipAddress the client's current IP address
     * @param userAgent the client's current User-Agent string
     * @return the new rotated RefreshToken entity
     */
    RefreshToken rotate(String rawToken, String ipAddress, String userAgent);

    /**
     * Revokes a refresh token in the database.
     *
     * @param rawToken the raw refresh token to revoke
     */
    void revokeToken(String rawToken);

    /**
     * Computes the HMAC-SHA256 hash of a raw token.
     *
     * @param rawToken the raw token string
     * @return the HMAC-SHA256 hash representation
     */
    String hashToken(String rawToken);
}
