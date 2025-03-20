package com.tesis.queseria_la_charito.repositories.dispatch;

import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchRepository extends JpaRepository<DispatchEntity, Long> {
  List<DispatchEntity> findByDestination(DestinationEntity destination);
  List<DispatchEntity> findByDestinationAndDate(DestinationEntity destination, LocalDate date);
  List<DispatchEntity> findByDestinationAndDateBetween(DestinationEntity destination, LocalDate startDate, LocalDate endDate);
  Optional<DispatchEntity> findByResponsibleAndStatusNot(UserEntity responsible, String status);
}
