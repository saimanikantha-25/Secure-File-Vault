package com.saimanikantha.securefilevault.security;

import com.saimanikantha.securefilevault.config.JwtProperties;
import com.saimanikantha.securefilevault.constants.SecurityConstants;
import com.saimanikantha.securefilevault.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key must not be null or empty.");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret key must be at least 256 bits (32 bytes) long. Provided secret is " 
                    + secretBytes.length * 8 + " bits.");
        }

        if (jwtProperties.getExpirationMs() <= 0) {
            throw new IllegalStateException("JWT expiration duration must be greater than zero.");
        }

        String issuer = jwtProperties.getIssuer();
        if (issuer == null || issuer.trim().isEmpty()) {
            throw new IllegalStateException("JWT issuer must not be null or empty.");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(user.getUsername())
                .claim(SecurityConstants.ROLE_CLAIM, user.getRole().name())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get(SecurityConstants.ROLE_CLAIM, String.class);
    }

    public Instant extractExpiration(String token) {
        return getClaims(token).getExpiration().toInstant();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).isBefore(Instant.now());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isTokenValid(String token, User user) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
