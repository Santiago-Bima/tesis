package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<BatchEntity, String> {
    List<BatchEntity> findByItemAndEstadoAndMostrar(ItemEntity item, String estado, boolean mostrar);
    List<BatchEntity> findByItem(ItemEntity item);
}
