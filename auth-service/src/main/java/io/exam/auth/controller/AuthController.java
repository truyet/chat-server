package io.exam.auth.controller;

import io.exam.auth.controller.body.LoginBody;
import io.exam.auth.domain.TokenInfo;
import io.exam.auth.service.AuthService;
import io.exam.rest.ResponseApi;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping()
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseApi<TokenInfo> login(@RequestBody LoginBody body) {
    return ResponseApi.success(authService.login(body.username(), body.password()));
  }


}
