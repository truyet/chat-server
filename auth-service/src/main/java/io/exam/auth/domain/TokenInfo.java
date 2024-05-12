package io.exam.auth.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenInfo {

  private String accessToken;
  private long expiredAt;

}
