package com.tesis.queseria_la_charito.repositories.purchase;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<ReceiptDetailEntity, Long> {
  List<ReceiptDetailEntity> findAllByReceipt(ReceiptEntity receipt);
  List<ReceiptDetailEntity> findAllByProviderSupplyAndReceiptDateBetween(ItemEntity item, LocalDate startDate, LocalDate finalDate);
}
