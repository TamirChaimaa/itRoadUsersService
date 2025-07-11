package com.itRoad.users_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Service for managing JWT (JSON Web Token) operations.
 * Handles token parsing, validation, and extracting claims.
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret:myVerySecureSecretKeyThatIsAtLeast32CharactersLongForHS256Algorithm}")
    private String secretKey;

    @Value("${jwt.expiration:86400}")
    private int jwtExpiration;

    private Key getSigningKey() {
        logger.debug("Creating signing key with secret length: {}", secretKey.length());
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        logger.debug("Extracting username from token");
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("Username extracted: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username: {}", e.getMessage());
            throw e;
        }
    }

    public String extractRole(String token) {
        logger.debug("Extracting role from token");
        try {
            String role = extractClaim(token, claims -> claims.get("role", String.class));
            logger.debug("Role extracted: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Error extracting role: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String username) {
        logger.debug("Validating token for username: {}", username);
        try {
            final String tokenUsername = extractUsername(token);
            boolean usernameMatch = tokenUsername.equals(username);
            boolean notExpired = !isTokenExpired(token);

            logger.debug("Username match: {}, Token not expired: {}", usernameMatch, notExpired);
            return usernameMatch && notExpired;
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateJwtToken(String authToken) {
        logger.debug("Validating JWT token");
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            logger.debug("Token validation successful");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            logger.debug("Token expiration: {}, Current time: {}", expiration, now);
            return expiration.before(now);
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("Extracting all claims from token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            logger.debug("Claims extracted successfully");
            return claims;
        } catch (Exception e) {
            logger.error("Error parsing token: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}