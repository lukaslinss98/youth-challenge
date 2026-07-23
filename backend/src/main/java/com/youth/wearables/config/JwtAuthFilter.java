package com.youth.wearables.config;

import com.youth.wearables.usermanagement.application.security.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class JwtAuthFilter extends OncePerRequestFilter {

  static final String USER_ID_ATTRIBUTE = "authenticatedUserId";
  private static final String BEARER_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;

  JwtAuthFilter(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith(BEARER_PREFIX)) {
      String token = header.substring(BEARER_PREFIX.length());
      tokenProvider
          .resolveUserId(token)
          .ifPresent(userId -> request.setAttribute(USER_ID_ATTRIBUTE, userId));
    }
    filterChain.doFilter(request, response);
  }
}
