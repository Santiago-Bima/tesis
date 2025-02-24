package com.tesis.queseria_la_charito.repositories.usuario;

import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
  Optional<UsuarioEntity> findByUsername(String username);
  Optional<UsuarioEntity> findByUsernameAndMostrar(String username, Boolean mostrar);
  List<UsuarioEntity> findByMostrarOrderByRolAsc(Boolean mostrar);
  Optional<UsuarioEntity> findByUsernameAndPasswordAndMostrar(String username, String password, Boolean mostrar);
}
