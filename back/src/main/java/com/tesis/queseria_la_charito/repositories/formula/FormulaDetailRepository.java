package com.tesis.queseria_la_charito.repositories.formula;

import com.tesis.queseria_la_charito.entities.formula.FormulaDetailEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulaDetailRepository extends JpaRepository<FormulaDetailEntity, Long> {
    List<FormulaDetailEntity> findAllByFormula(FormulaEntity formula);
}
