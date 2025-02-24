package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.despacho.DestinoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<DestinoEntity, Long> {
  Optional<DestinoEntity> findByCalleAndNumeroAndBarrio(String calle, Integer numero, String barrio);
}
