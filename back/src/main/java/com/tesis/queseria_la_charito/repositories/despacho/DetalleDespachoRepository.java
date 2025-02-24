package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.despacho.DespachoEntity;
import com.tesis.queseria_la_charito.entities.despacho.DetalleDespachoEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDespachoRepository extends JpaRepository<DetalleDespachoEntity, Long> {
  List<DetalleDespachoEntity> findByLote(LoteEntity lote);
  List<DetalleDespachoEntity> findAllByDespacho(DespachoEntity despacho);
}
