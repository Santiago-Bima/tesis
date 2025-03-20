package com.tesis.queseria_la_charito.repositories.batch;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, String> {
    List<BatchEntity> findByItemAndStatusAndShow(ItemEntity item, String status, boolean show);
    List<BatchEntity> findByItem(ItemEntity item);
}
