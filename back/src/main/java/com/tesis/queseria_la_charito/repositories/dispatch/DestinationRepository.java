package com.tesis.queseria_la_charito.repositories.dispatch;

import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<DestinationEntity, Long> {
  Optional<DestinationEntity> findByStreetAndNumberAndNeighborhood(String street, Integer number, String neighborhood);
}
