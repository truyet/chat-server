package io.exam.chat.config;

import io.exam.auth.JWTAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @ConditionalOnProperty("spring.jwt.shared-key")
  @Bean
  public JWTAuth jwtAuth(@Value("${spring.jwt.shared-key}") String sharedKey) {
    return new JWTAuth(sharedKey);
  }

  @Bean
  public FilterRegistrationBean<AuthenticationFilter> authFilter(JWTAuth jwtAuth) {
    AuthenticationFilter filter = new AuthenticationFilter(jwtAuth);
    return new FilterRegistrationBean<>(filter);
  }

}
