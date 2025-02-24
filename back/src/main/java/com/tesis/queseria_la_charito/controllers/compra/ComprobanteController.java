package com.tesis.queseria_la_charito.controllers.compra;

import com.tesis.queseria_la_charito.dtos.request.compra.ComprobanteCompraRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ComprobanteCompraResponse;
import com.tesis.queseria_la_charito.dtos.response.compra.InformeCompraResponse;
import com.tesis.queseria_la_charito.services.impls.CompraServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("comprobantes")
public class ComprobanteController {
  @Autowired
  private CompraServiceImpl service;

  @GetMapping("")
  List<ComprobanteCompraResponse> getAll(@RequestParam(required = false) LocalDate fecha) { return service.getAll(fecha.plusDays(1)); }

  @PostMapping("")
  ComprobanteCompraResponse post(@RequestBody ComprobanteCompraRequest request) { return service.post(request); }

  @GetMapping("/informes")
  List<InformeCompraResponse> generateInfome(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return service.generateInforme(fechaInicio, fechaFin); }
}
