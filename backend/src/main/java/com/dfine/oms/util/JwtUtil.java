package com.dfine.dfineoms.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // automatically generates a secure, cryptographic key for the HS256 algorithm
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // sets token expiration to 24 hours
    private final long jwtExpirationMs = 86400000;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // stores the user's role directly in the token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }
}