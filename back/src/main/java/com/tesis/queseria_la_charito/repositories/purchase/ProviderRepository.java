package com.tesis.queseria_la_charito.repositories.purchase;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {
  List<ProviderEntity> findBySupplyAndShow(ItemEntity item, boolean show);
  Optional<ProviderEntity> findByIdAndShow(Long id, boolean show);
}
