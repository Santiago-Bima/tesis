package com.tesis.queseria_la_charito.repositories.production;

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
public interface ProductionRepository extends JpaRepository<ProductionEntity, String> {

  List<ProductionEntity> findByResponsibleAndFormulaCheeseTypeItemAndDateBetween(UserEntity responsible, ItemEntity Item, LocalDate startDate, LocalDate endDate);
  List<ProductionEntity> findByResponsibleAndFormulaCheeseTypeItem(UserEntity responsible, ItemEntity item);
  Optional<ProductionEntity> findByBatch(BatchEntity batch);
  List<ProductionEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
  Optional<ProductionEntity> findByResponsibleAndId(UserEntity responsible, String id);
}
