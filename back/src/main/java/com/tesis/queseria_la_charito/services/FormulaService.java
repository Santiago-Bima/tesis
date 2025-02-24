package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.formula.FormulaRequest;
import com.tesis.queseria_la_charito.dtos.response.formula.FormulaResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FormulaService {
    List<FormulaResponse> getFormulasByProducto(Long productoId);
    FormulaResponse getFormulaById(String id);
    FormulaResponse postFormula(FormulaRequest formulaRequest);
    FormulaResponse putFormula(FormulaRequest formulaRequest, String id);
    FormulaResponse deleteFormula(String id);
}
