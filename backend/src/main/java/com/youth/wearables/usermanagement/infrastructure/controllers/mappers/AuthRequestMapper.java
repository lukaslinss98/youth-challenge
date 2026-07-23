package com.youth.wearables.usermanagement.infrastructure.controllers.mappers;

import com.youth.wearables.usermanagement.application.RegisterUserCommand;
import com.youth.wearables.usermanagement.infrastructure.controllers.dto.RegisterRequestDto;
import org.springframework.stereotype.Component;

@Component
public class AuthRequestMapper {

  public RegisterUserCommand toCommand(RegisterRequestDto dto) {
    return new RegisterUserCommand(dto.email(), dto.password());
  }
}
