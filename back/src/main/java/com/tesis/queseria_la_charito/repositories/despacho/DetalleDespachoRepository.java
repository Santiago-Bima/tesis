package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchDetailEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDespachoRepository extends JpaRepository<DispatchDetailEntity, Long> {
  List<DispatchDetailEntity> findByLote(BatchEntity lote);
  List<DispatchDetailEntity> findAllByDespacho(DispatchEntity despacho);
}
