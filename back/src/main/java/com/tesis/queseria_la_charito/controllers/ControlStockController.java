package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.dtos.request.controlStock.ControlStockRequest;
import com.tesis.queseria_la_charito.dtos.response.controlStock.CantidadesEsperadasResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.ControlStockResponse;
import com.tesis.queseria_la_charito.services.ControlStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("controles")
public class ControlStockController {
  @Autowired
  private ControlStockService service;

  @GetMapping("/validate")
  List<ControlStockResponse> getAllValidate() { return service.getAll(true); }

  @GetMapping("")
  List<ControlStockResponse> getAll() { return service.getAll(false); }

  @PostMapping()
  ControlStockResponse post(@RequestBody ControlStockRequest data) { return service.post(data); }

  @GetMapping("/valoresEsperados")
  CantidadesEsperadasResponse getEsperadas() { return service.getEsperado(); }
}
