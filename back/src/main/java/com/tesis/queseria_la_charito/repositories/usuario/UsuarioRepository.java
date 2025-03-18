package com.tesis.queseria_la_charito.repositories.usuario;

import com.tesis.queseria_la_charito.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  Optional<UserEntity> findByUsernameAndMostrar(String username, Boolean mostrar);
  List<UserEntity> findByMostrarOrderByRolAsc(Boolean mostrar);
  Optional<UserEntity> findByUsernameAndPasswordAndMostrar(String username, String password, Boolean mostrar);
}
