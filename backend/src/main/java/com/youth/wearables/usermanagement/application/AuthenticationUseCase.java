package com.youth.wearables.usermanagement.application;

import com.youth.wearables.usermanagement.application.repositories.UserRepository;
import com.youth.wearables.usermanagement.application.security.PasswordHasher;
import com.youth.wearables.usermanagement.domain.User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  public AuthenticationUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
  }

  public RegistrationResult register(RegisterUserCommand command) {
    if (userRepository.userByEmail(command.email()).isPresent()) {
      return new RegistrationResult.UserExists();
    }

    String passwordHash = passwordHasher.hash(command.password());
    User user = userRepository.createUser(command.email(), passwordHash);
    return new RegistrationResult.UserRegistered(user.id().toString(), user.email());
  }
}
