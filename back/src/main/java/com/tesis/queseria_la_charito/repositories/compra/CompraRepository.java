package com.tesis.queseria_la_charito.repositories.compra;

import com.tesis.queseria_la_charito.entities.compra.ComprobanteCompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<ComprobanteCompraEntity, Long> {
  List<ComprobanteCompraEntity> findAllByFecha(LocalDate fecha);
}
