package com.store.application.service;

import com.store.application.ApplicationConfig;
import com.store.application.port.in.JWTUseCase;
import com.store.domain.Role;
import com.store.domain.dto.UserClaims;
import com.store.domain.security.TokenResponse;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class JWTService implements JWTUseCase {
    private ApplicationConfig properties;

    @Override
    public TokenResponse buildToken(UserClaims mockUser) {
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", mockUser.getUserId().toString());
        claims.put("lastName", mockUser.getLastName());
        claims.put("email", mockUser.getEmail());
        claims.put("role", mockUser.getRole().toString());

        var now = new Date();
        var expiration = new Date(now.getTime() + properties.getExpiration());

        String token = Jwts.builder()
                .subject(mockUser.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .claims(claims)
                .compact();

        return new TokenResponse(token, mockUser.getUserId(), mockUser.getEmail(), expiration);
    }

    @Override
    public UserClaims verifyToken(String token) {
        var claims = Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        UserClaims res = new UserClaims();

        res.setEmail(claims.get("email").toString());
        res.setLastName(claims.get("lastName").toString());
        res.setRole(Role.valueOf(claims.get("role").toString()));
        res.setUserId(UUID.fromString(claims.get("userId").toString()));
        return res;
    }

    private SecretKey getKey() {
        byte[] bytes = properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }
}
