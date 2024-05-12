package io.exam.config;

import io.exam.auth.JWTAuth;
import io.exam.auth.UserContext;
import io.exam.auth.UserContext.UserInfo;
import io.exam.errors.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;


public class AuthenticationFilter extends OncePerRequestFilter {

  private final JWTAuth jwtAuth;

  public AuthenticationFilter(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var authVal = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!authVal.startsWith("Bearer ")) {
      throw new AuthenticationException("MISSING_TOKEN");
    }

    var token = authVal.substring(7);
    try {
      var payload = jwtAuth.decode(token);
      UserContext.setUser(UserInfo.builder().userId(payload.getLongClaim("sub")).username(payload.getStringClaim("username")).build());
    } catch (Exception e) {
      throw new AuthenticationException("INVALID_TOKEN");
    }
    filterChain.doFilter(request, response);

}

}