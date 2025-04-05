package com.haven.securities;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final Key SECRET_KEY;
    private static final long EXPIRATION_TIME = 3600000; // 1 giờ = 3600000 milliseconds

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        try {
            SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
            logger.info("JwtUtil initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing SECRET_KEY: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize JwtUtil", e);
        }
    }

    public String generateToken(UserDetails userDetails, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        // Thêm thông tin role vào claims
        claims.put("roles", userDetails.getAuthorities().isEmpty() ? "NO_ROLE" : userDetails.getAuthorities());
        claims.put("userId", userId);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", String.class);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public Integer extractUserId(String token) { // Thay extractManagerId bằng extractUserId để thống nhất
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            final Date expiration = extractAllClaims(token).getExpiration();
            
            // Kiểm tra username và thời hạn token
            return (username.equals(userDetails.getUsername()) 
                && !expiration.before(new Date()));
        } catch (Exception e) {
            logger.error("Error validating token: " + e.getMessage());
            return false;
        }
    }

    // Phương thức để lấy roles từ token
    public String getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles").toString();
    }

    // Kiểm tra token có hết hạn không
    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }
}