package com.tesis.queseria_la_charito.repositories.controlStock;

import com.tesis.queseria_la_charito.entities.controlStock.ControlStockEntity;
import com.tesis.queseria_la_charito.entities.controlStock.InsumoControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsumoControlRepository extends JpaRepository<InsumoControlEntity, Long> {
  List<InsumoControlEntity> findByControlStockAndTipo(ControlStockEntity controlStockEntity, String tipo);
}
