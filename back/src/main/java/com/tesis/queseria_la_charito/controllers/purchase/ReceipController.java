package com.tesis.queseria_la_charito.controllers.purchase;

import com.tesis.queseria_la_charito.dtos.request.purchase.ReceipRequest;
import com.tesis.queseria_la_charito.dtos.response.purchase.ReceipResponse;
import com.tesis.queseria_la_charito.dtos.response.purchase.ReceipReportResponse;
import com.tesis.queseria_la_charito.services.compras.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("comprobantes")
public class ReceipController {
  @Autowired
  private CompraService service;


  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @GetMapping("")
  List<ReceipResponse> getAll(@RequestParam(required = false) LocalDate fecha) { return service.getAll(fecha.plusDays(1)); }

  @PreAuthorize("hasRole('ROLE_Subgerente')")
  @PostMapping("")
  ReceipResponse post(@RequestBody ReceipRequest request) { return service.post(request); }

  @PreAuthorize("hasRole('ROLE_Gerente')")
  @GetMapping("/informes")
  List<ReceipReportResponse> generateReport(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return service.generateInforme(fechaInicio, fechaFin); }
}
