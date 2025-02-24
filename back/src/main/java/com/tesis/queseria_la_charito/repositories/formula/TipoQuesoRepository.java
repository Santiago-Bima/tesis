package com.tesis.queseria_la_charito.repositories.formula;

import com.tesis.queseria_la_charito.entities.formula.TipoQuesoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoQuesoRepository extends JpaRepository<TipoQuesoEntity, Long> {
}
