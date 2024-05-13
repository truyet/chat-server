package io.exam.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jose.JOSEException;
import io.exam.auth.JWTAuth;
import io.exam.auth.config.BeanConfig;
import io.exam.auth.domain.User;
import io.exam.auth.repositories.UserRepository;
import io.exam.errors.ServiceException;
import io.exam.utils.PasswordUtils;
import java.text.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(basePackages = {"io.exam.auth.repositories"})
@EntityScan(basePackages = {"io.exam.auth.domain"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {BeanConfig.class, UserRepository.class, AuthService.class})
class AuthServiceTest {

  @Autowired
  private AuthService authService;

  @Autowired
  private JWTAuth jwtAuth;

  @Autowired
  private UserRepository userRepository;

  private final User defaultUser = User.builder().username("default").password("123456").build();

  @BeforeEach
  void setUp() {
    userRepository.save(User.builder().username(defaultUser.getUsername())
        .password(PasswordUtils.passwordHash(defaultUser.getPassword())).build());
  }

  @Test
  void login_whenUserNotExist_shouldCreateUser() throws ParseException, JOSEException {
    var username = "test";
    var tokenInfo = authService.login(username, "test");
    var claims = jwtAuth.decode(tokenInfo.getAccessToken());
    assertEquals(claims.getStringClaim("username"), username);
  }

  @Test
  void login_whenUserExist_shouldLogin() throws ParseException, JOSEException {
    var tokenInfo = authService.login(defaultUser.getUsername(), defaultUser.getPassword());
    var claims = jwtAuth.decode(tokenInfo.getAccessToken());
    assertEquals(claims.getStringClaim("username"), defaultUser.getUsername());
  }

  @Test
  void login_whenPasswordNotMatch_shouldThrowException() {
    assertThrows(ServiceException.class,
        () -> authService.login(defaultUser.getUsername(), "1111"));
  }
}