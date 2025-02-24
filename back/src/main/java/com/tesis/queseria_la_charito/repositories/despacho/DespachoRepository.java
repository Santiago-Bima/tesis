package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.despacho.DespachoEntity;
import com.tesis.queseria_la_charito.entities.despacho.DestinoEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<DespachoEntity, Long> {
  List<DespachoEntity> findByDestino(DestinoEntity destino);
  List<DespachoEntity> findByDestinoAndFecha(DestinoEntity destino, LocalDate fecha);
  List<DespachoEntity> findByDestinoAndFechaBetween(DestinoEntity destino, LocalDate fechaInicio, LocalDate fechaFin);
  Optional<DespachoEntity> findByUsuarioAndEstadoNot(UsuarioEntity usuario, String estado);
}
