package com.tesis.queseria_la_charito.repositories.formula;

import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.formula.TipoQuesoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulaRepository extends JpaRepository<FormulaEntity, String> {
    List<FormulaEntity> findAllByTipoQueso(TipoQuesoEntity producto);
}
