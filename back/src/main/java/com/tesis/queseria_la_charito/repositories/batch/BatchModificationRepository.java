package com.tesis.queseria_la_charito.repositories.batch;

import com.tesis.queseria_la_charito.entities.batch.BatchModificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchModificationRepository extends JpaRepository<BatchModificationEntity, Long> {
  List<BatchModificationEntity> findAllByOrderByDateDescIdDesc();
}
