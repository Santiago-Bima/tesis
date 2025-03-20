package com.tesis.queseria_la_charito.repositories.purchase;

import com.tesis.queseria_la_charito.entities.purchase.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<ReceiptEntity, Long> {
  List<ReceiptEntity> findAllByDate(LocalDate date);
}
