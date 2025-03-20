package com.tesis.queseria_la_charito.repositories;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByType(String type);
    Optional<ItemEntity> findByName(String name);
}
