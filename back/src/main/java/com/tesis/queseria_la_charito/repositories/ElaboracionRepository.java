package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ElaboracionRepository extends JpaRepository<ProductionEntity, String> {

  List<ProductionEntity> findByUsuarioAndFormulaTipoQuesoItemAndFechaBetween(UserEntity usuario, ItemEntity Item, LocalDate fechaInicio, LocalDate fechaFin);
  List<ProductionEntity> findByUsuarioAndFormulaTipoQuesoItem(UserEntity usuario, ItemEntity item);
  Optional<ProductionEntity> findByLote(BatchEntity lote);
  List<ProductionEntity> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
  Optional<ProductionEntity> findByUsuarioAndId(UserEntity usuario, String id);
}
