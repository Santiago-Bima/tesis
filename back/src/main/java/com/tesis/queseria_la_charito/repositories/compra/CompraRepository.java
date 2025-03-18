package com.tesis.queseria_la_charito.repositories.compra;

import com.tesis.queseria_la_charito.entities.purchase.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<ReceiptEntity, Long> {
  List<ReceiptEntity> findAllByFecha(LocalDate fecha);
}
