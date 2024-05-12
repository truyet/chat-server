package io.exam.auth;

import java.security.Principal;
import java.util.function.Supplier;
import javax.security.auth.Subject;
import lombok.Builder;
import lombok.Data;

public class UserContext {

  private static final ThreadLocal<UserInfo> contextHolder = new ThreadLocal<>();

  public static UserInfo getUser() {
    return contextHolder.get();
  }

  public static void setUser(UserInfo userInfo) {
    contextHolder.set(userInfo);
  }

  @Builder
  @Data
  public static class UserInfo implements Principal {
    private long userId;
    private String username;

    @Override
    public String getName() {
      return username;
    }

    @Override
    public boolean implies(Subject subject) {
      return Principal.super.implies(subject);
    }
  }

}
