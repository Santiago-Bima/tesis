package com.tesis.queseria_la_charito.repositories.compra;

import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.compra.ComprobanteCompraEntity;
import com.tesis.queseria_la_charito.entities.compra.DetalleComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleComprobanteEntity, Long> {
  List<DetalleComprobanteEntity> findAllByComprobante(ComprobanteCompraEntity comprobanteCompra);
  List<DetalleComprobanteEntity> findAllByProveedorInsumoAndComprobanteFechaBetween(ItemEntity item, LocalDate fechaInicio, LocalDate fechaFin);
}
