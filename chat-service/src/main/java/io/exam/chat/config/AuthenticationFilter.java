package io.exam.chat.config;

import io.exam.auth.JWTAuth;
import io.exam.auth.UserContext;
import io.exam.auth.UserContext.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


public class AuthenticationFilter extends OncePerRequestFilter {

  private final JWTAuth jwtAuth;

  public AuthenticationFilter(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    AntPathMatcher pathMatcher = new AntPathMatcher();
    if (pathMatcher.match("/swagger-ui/**", request.getServletPath())) {
      filterChain.doFilter(request, response);
      return;
    }
    if (pathMatcher.match("/v3/api-docs/**", request.getServletPath())) {
      filterChain.doFilter(request, response);
      return;
    }
    if (pathMatcher.match("/ws", request.getServletPath())) {
      filterChain.doFilter(request, response);
      return;
    }
    var authVal = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authVal == null || !authVal.startsWith("Bearer ")) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return;
    }

    var token = authVal.substring(7);
    try {
      var payload = jwtAuth.decode(token);
      UserContext.setUser(UserInfo.builder().userId(Long.parseLong(payload.getStringClaim("sub")))
          .username(payload.getStringClaim("username")).build());
    } catch (Exception e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return;
    }
    filterChain.doFilter(request, response);

  }

}