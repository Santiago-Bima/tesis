package com.tesis.queseria_la_charito.repositories.compra;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<ReceiptDetailEntity, Long> {
  List<ReceiptDetailEntity> findAllByComprobante(ReceiptEntity comprobanteCompra);
  List<ReceiptDetailEntity> findAllByProveedorInsumoAndComprobanteFechaBetween(ItemEntity item, LocalDate fechaInicio, LocalDate fechaFin);
}
