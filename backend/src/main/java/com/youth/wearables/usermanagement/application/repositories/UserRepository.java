package com.youth.wearables.usermanagement.application.repositories;

import com.youth.wearables.usermanagement.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  Optional<User> user(UUID userId);

  Optional<User> userByEmail(String email);

  User createUser(String email, String username, String passwordHash);
}
