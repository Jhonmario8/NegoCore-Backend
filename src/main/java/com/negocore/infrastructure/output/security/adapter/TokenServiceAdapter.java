package com.negocore.infrastructure.output.security.adapter;

import com.negocore.domain.api.ITokenServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class TokenServiceAdapter implements ITokenServicePort {

    @Value("${spring.security.jwt.secret}")
    private String secretKey;
    @Value("${spring.security.jwt.expiration}")
    private long expirationTime;

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .claims(Map.of(
                        DomainConstants.KEY_USER_ID, user.getId()
                ))
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
