package com.tesis.queseria_la_charito.repositories.formula;

import com.tesis.queseria_la_charito.entities.formula.CheeseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheeseTypeRepository extends JpaRepository<CheeseTypeEntity, Long> {
}
