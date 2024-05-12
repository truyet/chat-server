package io.exam.auth.repositories;

import io.exam.auth.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
  @Query("FROM User u WHERE u.username = :username ORDER BY id DESC LIMIT 1")
  Optional<User> getUserByUsername(String username);

}
