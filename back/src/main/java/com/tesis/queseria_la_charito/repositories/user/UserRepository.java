package com.tesis.queseria_la_charito.repositories.user;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  Optional<UserEntity> findByUsernameAndShow(String username, Boolean show);
  List<UserEntity> findByShowOrderByRoleAsc(Boolean show);
  Optional<UserEntity> findByUsernameAndPasswordAndShow(String username, String password, Boolean show);
}
