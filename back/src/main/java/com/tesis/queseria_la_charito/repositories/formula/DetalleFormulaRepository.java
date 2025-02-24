package com.tesis.queseria_la_charito.repositories.formula;

import com.tesis.queseria_la_charito.entities.formula.DetalleFormulaEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFormulaRepository extends JpaRepository<DetalleFormulaEntity, Long> {
    List<DetalleFormulaEntity> findAllByFormula(FormulaEntity formula);
}
