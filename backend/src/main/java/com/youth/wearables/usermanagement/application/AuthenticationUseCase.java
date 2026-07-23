package com.youth.wearables.usermanagement.application;

import com.youth.wearables.usermanagement.application.repositories.UserRepository;
import com.youth.wearables.usermanagement.application.security.PasswordHasher;
import com.youth.wearables.usermanagement.application.security.TokenProvider;
import com.youth.wearables.usermanagement.domain.LoginResult;
import com.youth.wearables.usermanagement.domain.LoginUserCommand;
import com.youth.wearables.usermanagement.domain.RegisterUserCommand;
import com.youth.wearables.usermanagement.domain.RegistrationResult;
import com.youth.wearables.usermanagement.domain.User;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final TokenProvider tokenProvider;

  public AuthenticationUseCase(
      UserRepository userRepository, PasswordHasher passwordHasher, TokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.tokenProvider = tokenProvider;
  }

  public RegistrationResult register(RegisterUserCommand command) {
    if (userRepository.userByEmail(command.email()).isPresent()) {
      return new RegistrationResult.UserExists();
    }

    String passwordHash = passwordHasher.hash(command.password());
    User user = userRepository.createUser(command.email(), passwordHash);
    String token = tokenProvider.issueToken(user.id());
    return new RegistrationResult.UserRegistered(user.id().toString(), user.email(), token);
  }

  public LoginResult login(LoginUserCommand command) {
    Optional<User> user = userRepository.userByEmail(command.email());
    if (user.isEmpty() || !passwordHasher.matches(command.password(), user.get().passwordHash())) {
      return new LoginResult.InvalidCredentials();
    }

    String token = tokenProvider.issueToken(user.get().id());
    return new LoginResult.Success(user.get().id().toString(), user.get().email(), token);
  }
}
