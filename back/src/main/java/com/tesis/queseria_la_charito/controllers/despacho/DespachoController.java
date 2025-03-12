package com.tesis.queseria_la_charito.controllers.despacho;

import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoRequest;
import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DespachoResponse;
import com.tesis.queseria_la_charito.dtos.response.despacho.InformeDespachoResponse;
import com.tesis.queseria_la_charito.services.despachos.DespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/despachos")
public class DespachoController {
  @Autowired
  private DespachoService despachoService;

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @GetMapping("")
  List<DespachoResponse> getAll(@RequestParam(required = false) LocalDate fecha, @RequestParam Long destinoId) { return despachoService.getAll(fecha, destinoId); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/{id}")
  DespachoResponse getById(@PathVariable Long id) { return despachoService.getById(id); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PostMapping("")
  DespachoResponse post(@RequestBody DespachoRequest despachoRequest) { return despachoService.post(despachoRequest); }

  @PreAuthorize("hasRole('ROLE_Subgerente'")
  @PutMapping("/{id}")
  DespachoResponse put(@PathVariable Long id, @RequestBody DespachoUpdateRequest despachoUpdateRequest) { return despachoService.put(despachoUpdateRequest, id); }

  @PreAuthorize("hasAnyRole('ROLE_Subgerente', 'ROLE_Operario'")
  @DeleteMapping("/{id}")
  DespachoResponse delete(@PathVariable Long id) { return despachoService.delete(id); }

  @PreAuthorize("hasAnyRole('ROLE_Gerente'")
  @GetMapping("/informes")
  InformeDespachoResponse generateInfome(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return despachoService.generateInforme(fechaInicio, fechaFin); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/mi-despacho/{username}")
  List<DespachoResponse> getByUser(@PathVariable String username) { return despachoService.getByUser(username); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/cambiar-estado/{id}")
  DespachoResponse changeStatus(@PathVariable Long id) { return despachoService.changeEstado(id); }
}
