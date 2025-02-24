package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.ElaboracionEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ElaboracionRepository extends JpaRepository<ElaboracionEntity, String> {

  List<ElaboracionEntity> findByUsuarioAndFormulaTipoQuesoItemAndFechaBetween(UsuarioEntity usuario,ItemEntity Item, LocalDate fechaInicio, LocalDate fechaFin);
  List<ElaboracionEntity> findByUsuarioAndFormulaTipoQuesoItem(UsuarioEntity usuario, ItemEntity item);
  Optional<ElaboracionEntity> findByLote(LoteEntity lote);
  List<ElaboracionEntity> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
  Optional<ElaboracionEntity> findByUsuarioAndId(UsuarioEntity usuario, String id);
}
