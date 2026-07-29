package com.saimanikantha.securefilevault.repository;

import com.saimanikantha.securefilevault.entity.RefreshToken;
import com.saimanikantha.securefilevault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link RefreshToken} persistence operations.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token record by its HMAC-SHA256 hash.
     *
     * @param tokenHash the HMAC hash of the refresh token
     * @return an optional containing the found token, or empty
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Finds all non-revoked and non-expired refresh tokens for a user.
     *
     * @param user the user whose tokens to retrieve
     * @param now  the current timestamp to check expiration
     * @return a list of active refresh tokens
     */
    List<RefreshToken> findByUserAndRevokedFalseAndExpiryDateAfter(User user, Instant now);

    /**
     * Finds all refresh tokens belonging to a specific token family.
     *
     * @param familyId the unique token family identifier
     * @return a list of refresh tokens in the family
     */
    List<RefreshToken> findByFamilyId(String familyId);

    /**
     * Prunes expired refresh tokens from the database. Deleted records are either
     * older than the retention threshold (for audit purposes) or revoked and expired past the expiry threshold.
     *
     * @param retentionThreshold the threshold timestamp before which all tokens are deleted
     * @param expiryThreshold    the threshold timestamp before which revoked and expired tokens are deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiryDate < :retentionThreshold OR (r.expiryDate < :expiryThreshold AND (r.revoked = true OR r.reuseDetected = true))")
    void deleteExpiredAndRevokedTokens(
            @Param("retentionThreshold") Instant retentionThreshold,
            @Param("expiryThreshold") Instant expiryThreshold
    );
}
