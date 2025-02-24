package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<LoteEntity, String> {
    List<LoteEntity> findByItemAndEstadoAndMostrar(ItemEntity item, String estado, boolean mostrar);
    List<LoteEntity> findByItem(ItemEntity item);
}
