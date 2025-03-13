package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.dtos.request.stockControl.StockControlRequest;
import com.tesis.queseria_la_charito.dtos.response.stockControl.ExpectedQuantityResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.StockControlResponse;
import com.tesis.queseria_la_charito.services.ControlStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("controles")
public class StockControlController {
  @Autowired
  private ControlStockService service;


//  TODO: Configrmar roles requeridos
  @GetMapping("/validate")
  List<StockControlResponse> getAllValidate() { return service.getAll(true); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("")
  List<StockControlResponse> getAll() { return service.getAll(false); }

  @PreAuthorize("hasRole('ROLE_Operario')")
  @PostMapping()
  StockControlResponse post(@RequestBody StockControlRequest data) { return service.post(data); }

  @PreAuthorize("hasRole('ROLE_Operario')")
  @GetMapping("/valoresEsperados")
  ExpectedQuantityResponse getExpected() { return service.getEsperado(); }
}
