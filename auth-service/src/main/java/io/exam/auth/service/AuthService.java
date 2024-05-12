package io.exam.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import io.exam.auth.domain.TokenInfo;
import io.exam.auth.domain.User;
import io.exam.auth.repositories.UserRepository;
import io.exam.errors.ServiceException;
import io.exam.auth.JWTAuth;
import io.exam.utils.PasswordUtils;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  private final JWTAuth jwtUtils;

  public TokenInfo login(String userName, String password) {
    var user = userRepository.getUserByUsername(userName);
    if (user.isEmpty()) {
      var newUser = User.builder().username(userName).password(PasswordUtils.passwordHash(password)).build();
      newUser = userRepository.save(newUser);
      user = Optional.of(newUser);

    } else {
      var curUser = user.get();
      if (!PasswordUtils.isMatchedPassword(password, curUser.getPassword())) {
        throw new ServiceException("MISMATCH_USER_PASSWORD");
      }
    }

    var expireAt = new Date(System.currentTimeMillis() + 86400000);
    var curUser = user.get();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(String.valueOf(curUser.getId()))
        .expirationTime(expireAt)
        .claim("username", curUser.getUsername())
        .build();
    try {
      return TokenInfo.builder().accessToken(jwtUtils.encode(claimsSet)).expiredAt(expireAt.getTime()).build();
    } catch (Exception e) {
      log.error("Token error ", e);
      throw new ServiceException(e.getMessage());
    }


  }

}
