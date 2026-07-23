package com.youth.wearables.usermanagement.infrastructure.persistence;

import com.youth.wearables.usermanagement.application.repositories.UserRepository;
import com.youth.wearables.usermanagement.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  UserRepositoryImpl(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public Optional<User> user(UUID userId) {
    return userJpaRepository.findById(userId).map(this::toDomain);
  }

  @Override
  public Optional<User> userByEmail(String email) {
    return userJpaRepository.findByEmail(email).map(this::toDomain);
  }

  @Override
  public User createUser(String email, String passwordHash) {
    User user = User.register(email, passwordHash);
    UserV1 entity =
        new UserV1(user.id(), user.email(), user.passwordHash(), user.createdAt());
    userJpaRepository.save(entity);
    return user;
  }

  private User toDomain(UserV1 entity) {
    return new User(
        entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getCreatedAt());
  }
}
