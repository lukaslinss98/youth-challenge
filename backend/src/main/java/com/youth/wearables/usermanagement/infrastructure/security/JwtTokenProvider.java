package com.youth.wearables.usermanagement.infrastructure.security;

import com.youth.wearables.usermanagement.application.security.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class JwtTokenProvider implements TokenProvider {

  private final Key signingKey;
  private final long expirationMs;

  JwtTokenProvider(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  @Override
  public String issueToken(UUID userId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(signingKey)
        .compact();
  }
}
