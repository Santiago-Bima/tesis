package com.tesis.queseria_la_charito.repositories.stockControl;

import com.tesis.queseria_la_charito.entities.stockControl.StockControlEntity;
import com.tesis.queseria_la_charito.entities.stockControl.InsumoControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyControlRepository extends JpaRepository<InsumoControlEntity, Long> {
  List<InsumoControlEntity> findByStockControlAndType(StockControlEntity stockControlEntity, String type);
}
