package com.saimanikantha.securefilevault.entity;

/**
 * Enumeration representing auditable authentication events in the system.
 */
public enum AuthEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    REFRESH_SUCCESS,
    REFRESH_FAILURE,
    LOGOUT,
    TOKEN_REUSE_DETECTED
}
