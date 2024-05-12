package io.exam.errors;

public class AuthenticationException extends RuntimeException {

  public AuthenticationException(String msg) {
    super(msg);
  }

}
