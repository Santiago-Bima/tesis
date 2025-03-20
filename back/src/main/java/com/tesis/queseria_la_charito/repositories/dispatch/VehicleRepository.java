package com.tesis.queseria_la_charito.repositories.dispatch;

import com.tesis.queseria_la_charito.entities.dispatch.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
  Optional<VehicleEntity> findByPlate(String plate);
}
