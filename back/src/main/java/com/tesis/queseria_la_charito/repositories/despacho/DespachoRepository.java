package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<DispatchEntity, Long> {
  List<DispatchEntity> findByDestino(DestinationEntity destino);
  List<DispatchEntity> findByDestinoAndFecha(DestinationEntity destino, LocalDate fecha);
  List<DispatchEntity> findByDestinoAndFechaBetween(DestinationEntity destino, LocalDate fechaInicio, LocalDate fechaFin);
  Optional<DispatchEntity> findByUsuarioAndEstadoNot(UserEntity usuario, String estado);
}
