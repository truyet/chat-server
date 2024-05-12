package io.exam.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class PasswordUtils {
  private static final Argon2PasswordEncoder arg2SpringSecurity = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);

  public static String generateHash() {
    return KeyGenerators.string().generateKey();
  }

  public static String passwordHash(String password) {
    return arg2SpringSecurity.encode(password);
  }

  public static boolean isMatchedPassword(String password, String encodedPassword) {
    return arg2SpringSecurity.matches(password, encodedPassword);
  }


}
