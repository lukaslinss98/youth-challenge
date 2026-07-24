package com.youth.wearables.integration.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public class TestJwtIssuer {

  private final SecretKey signingKey;
  private final long expirationMs;

  public TestJwtIssuer(String secret, long expirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

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
