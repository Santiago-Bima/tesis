package com.tesis.queseria_la_charito.repositories.controlStock;

import com.tesis.queseria_la_charito.entities.stockControl.StockControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlStockRepository extends JpaRepository<StockControlEntity, Long> {
  List<StockControlEntity> findAllByOrderByFechaDescIdDesc();
}
