package com.youth.wearables.usermanagement.infrastructure.controllers;

import com.youth.wearables.usermanagement.application.AuthenticationUseCase;
import com.youth.wearables.usermanagement.domain.LoginResult;
import com.youth.wearables.usermanagement.domain.LoginResult.InvalidCredentials;
import com.youth.wearables.usermanagement.domain.LoginResult.Success;
import com.youth.wearables.usermanagement.domain.LoginUserCommand;
import com.youth.wearables.usermanagement.domain.RegisterUserCommand;
import com.youth.wearables.usermanagement.domain.RegistrationResult;
import com.youth.wearables.usermanagement.domain.RegistrationResult.UserExists;
import com.youth.wearables.usermanagement.domain.RegistrationResult.UserRegistered;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.AuthResponseDto;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.LoginRequestDto;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.RegisterRequestDto;
import com.youth.wearables.usermanagement.infrastructure.controllers.mappers.AuthRequestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

  private final AuthRequestMapper authRequestMapper;
  private final AuthenticationUseCase authenticationUseCase;

  public AuthController(
      AuthRequestMapper authRequestMapper, AuthenticationUseCase authenticationUseCase) {
    this.authRequestMapper = authRequestMapper;
    this.authenticationUseCase = authenticationUseCase;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
    RegisterUserCommand command = authRequestMapper.toCommand(request);
    RegistrationResult result = authenticationUseCase.register(command);

    return switch (result) {
      case UserRegistered(String userId, String email, String token) ->
          ResponseEntity.status(HttpStatus.CREATED)
              .body(new AuthResponseDto(new AuthResponseDto.UserDto(userId, email), token));
      case UserExists ignore -> ResponseEntity.status(HttpStatus.CONFLICT).build();
    };
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
    LoginUserCommand command = authRequestMapper.toCommand(request);
    LoginResult result = authenticationUseCase.login(command);

    return switch (result) {
      case Success(String userId, String email, String token) ->
          ResponseEntity.ok(new AuthResponseDto(new AuthResponseDto.UserDto(userId, email), token));
      case InvalidCredentials ignore -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    };
  }
}
