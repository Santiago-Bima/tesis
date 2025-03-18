package com.tesis.queseria_la_charito.repositories.despacho;

import com.tesis.queseria_la_charito.entities.dispatch.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<VehicleEntity, Long> {
  Optional<VehicleEntity> findByMatricula(String matricula);
}
