package com.tesis.queseria_la_charito.controllers.formula;

import com.tesis.queseria_la_charito.dtos.request.formula.FormulaRequest;
import com.tesis.queseria_la_charito.dtos.response.formula.FormulaResponse;
import com.tesis.queseria_la_charito.services.impls.FormulaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("formulas")
public class FormulaController {
    @Autowired
    private FormulaServiceImpl formulaService;

    @GetMapping("/listarBy/{tipoProductoId}")
    public List<FormulaResponse> getAll(@PathVariable Long tipoProductoId) { return formulaService.getFormulasByProducto(tipoProductoId); }

    @GetMapping("/{id}")
    public FormulaResponse getById(@PathVariable String id) { return formulaService.getFormulaById(id); }

    @PostMapping("")
    public FormulaResponse post(@RequestBody FormulaRequest formula) { return formulaService.postFormula(formula); }

    @PutMapping("/{id}")
    public FormulaResponse put(@PathVariable String id, @RequestBody FormulaRequest formulaRequest) { return formulaService.putFormula(formulaRequest, id); }

    @DeleteMapping("/{id}")
    private FormulaResponse delete(@PathVariable String id) { return formulaService.deleteFormula(id); }

}
