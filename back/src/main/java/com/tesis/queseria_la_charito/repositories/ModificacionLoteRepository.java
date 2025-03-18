package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.batch.BatchModificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificacionLoteRepository extends JpaRepository<BatchModificationEntity, Long> {
  List<BatchModificationEntity> findAllByOrderByFechaDescIdDesc();
}
