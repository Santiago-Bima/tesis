package com.tesis.queseria_la_charito.repositories.controlStock;

import com.tesis.queseria_la_charito.entities.controlStock.ControlStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlStockRepository extends JpaRepository<ControlStockEntity, Long> {
  List<ControlStockEntity> findAllByOrderByFechaDescIdDesc();
}
