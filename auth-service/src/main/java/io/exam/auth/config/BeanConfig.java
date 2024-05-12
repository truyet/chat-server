package io.exam.auth.config;

import io.exam.auth.JWTAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @ConditionalOnProperty("spring.jwt.shared-key")
  @Bean
  public JWTAuth jwtAuth(@Value("${spring.jwt.shared-key}") String sharedKey) {
    return new JWTAuth(sharedKey);
  }

}
