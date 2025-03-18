package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<DestinationEntity, Long> {
  Optional<DestinationEntity> findByCalleAndNumeroAndBarrio(String calle, Integer numero, String barrio);
}
