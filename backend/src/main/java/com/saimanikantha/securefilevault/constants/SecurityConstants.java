package com.saimanikantha.securefilevault.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    public static final String TOKEN_TYPE = "Bearer";
    public static final String ROLE_CLAIM = "role";

    public static final String[] PUBLIC_URLS = {
            "/actuator/health",
            "/actuator/**",
            "/api/v1/users/register",
            "/api/v1/auth/**",
            "/api/v1/test/public"
    };

}
