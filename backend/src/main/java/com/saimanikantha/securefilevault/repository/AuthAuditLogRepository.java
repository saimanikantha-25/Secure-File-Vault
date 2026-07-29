package com.saimanikantha.securefilevault.repository;

import com.saimanikantha.securefilevault.entity.AuthAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AuthAuditLog} persistence operations.
 */
@Repository
public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, Long> {
}
