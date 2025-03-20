package com.tesis.queseria_la_charito.repositories.user;

import com.tesis.queseria_la_charito.entities.user.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findByRole(String name);
}
